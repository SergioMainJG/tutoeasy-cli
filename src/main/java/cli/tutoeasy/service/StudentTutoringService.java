package cli.tutoeasy.service;

import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.*;
import cli.tutoeasy.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling student tutoring operations.
 * This service manages tutoring requests from the student perspective,
 * including creating, viewing, updating, and canceling tutoring sessions.
 */
public class StudentTutoringService {

    private final TutoringRepository tutoringRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ContactRepository contactRepository;
    private final NotificationService notificationService;

    /**
     * Constructor with dependency injection
     *
     * @param tutoringRepository Repository for tutoring operations
     * @param userRepository Repository for user operations
     * @param subjectRepository Repository for subject operations
     * @param contactRepository Repository for contact operations
     * @param notificationService Service for notification operations
     */
    public StudentTutoringService(
            TutoringRepository tutoringRepository,
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            ContactRepository contactRepository,
            NotificationService notificationService) {
        this.tutoringRepository = tutoringRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.contactRepository = contactRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new tutoring request.
     * Validates all inputs, checks for conflicts, and notifies the tutor.
     *
     * @param studentId ID of the student requesting the tutoring
     * @param dto Data transfer object with tutoring details
     * @return ActionResponseDto indicating success or failure
     */
    public ActionResponseDto createTutoringRequest(int studentId, CreateTutoringRequestDto dto) {
        // Validate date is not in the past
        LocalDate today = LocalDate.now();
        if (dto.meetingDate().isBefore(today)) {
            return new ActionResponseDto(false, "Cannot create tutoring for past dates.");
        }

        // Validate student exists
        User student = userRepository.findById(studentId);
        if (student == null) {
            return new ActionResponseDto(false, "Student not found.");
        }

        // Validate tutor exists
        User tutor = contactRepository.findByUsername(dto.tutorUsername());
        if (tutor == null) {
            return new ActionResponseDto(false, "Tutor not found: " + dto.tutorUsername());
        }

        // Validate tutor role
        if (tutor.getRol() != UserRole.tutor) {
            return new ActionResponseDto(false, dto.tutorUsername() + " is not a tutor.");
        }

        // Find subject by ID or name
        Subject subject;
        try {
            int subjectId = Integer.parseInt(dto.subjectNameOrId());
            subject = subjectRepository.findById(subjectId);
        } catch (NumberFormatException e) {
            subject = subjectRepository.findByName(dto.subjectNameOrId());
        }

        if (subject == null) {
            return new ActionResponseDto(false, "Subject not found: " + dto.subjectNameOrId());
        }

        // Check for schedule conflicts
        if (tutoringRepository.hasScheduleConflict(tutor.getId(), dto.meetingDate(), dto.meetingTime())) {
            return new ActionResponseDto(false, "Tutor already has a confirmed session at that time.");
        }

        // Create tutoring entity
        Tutoring tutoring = new Tutoring();
        tutoring.setStudent(student);
        tutoring.setTutor(tutor);
        tutoring.setSubject(subject);
        tutoring.setMeetingDate(dto.meetingDate());
        tutoring.setMeetingTime(dto.meetingTime());
        tutoring.setStatus(TutoringStatus.unconfirmed);

        // Handle topic (if provided)
        if (dto.topicName() != null && !dto.topicName().trim().isEmpty()) {
            // Topic handling logic here if needed
        }

        // Save tutoring request
        tutoringRepository.save(tutoring);

        // Send notification to tutor
        String notificationMessage = String.format(
                "New tutoring request from %s for %s on %s at %s",
                student.getUsername(),
                subject.getName(),
                dto.meetingDate(),
                dto.meetingTime());
        notificationService.addNotification(tutor.getId(), notificationMessage, "TUTORING_REQUEST");

        return new ActionResponseDto(true, "Tutoring request created successfully. Waiting for tutor confirmation.");
    }

    /**
     * Gets all upcoming tutoring sessions for a student.
     * Only returns sessions that are not canceled and have a meeting date >= today.
     *
     * @param studentId ID of the student
     * @return List of StudentTutoringDto with tutoring details
     */
    public List<StudentTutoringDto> getUpcomingTutorings(int studentId) {
        List<Tutoring> tutorings = tutoringRepository.findUpcomingByStudent(studentId);

        return tutorings.stream()
                .map(t -> new StudentTutoringDto(
                        t.getId(),
                        t.getTutor().getUsername(),
                        t.getSubject().getName(),
                        t.getTopic() != null ? t.getTopic().getName() : null,
                        t.getMeetingDate(),
                        t.getMeetingTime(),
                        t.getStatus().name()))
                .collect(Collectors.toList());
    }

    /**
     * Cancels a tutoring session.
     * Only the student who created the request can cancel it.
     * Cannot cancel completed or already canceled sessions.
     *
     * @param studentId ID of the student
     * @param tutoringId ID of the tutoring to cancel
     * @return ActionResponseDto indicating success or failure
     */
    public ActionResponseDto cancelTutoring(int studentId, int tutoringId) {
        // Load tutoring with all relationships
        Tutoring tutoring;
        try {
            tutoring = tutoringRepository.findByIdWithDetails(tutoringId);
        } catch (Exception e) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getStudent().getId() != studentId) {
            return new ActionResponseDto(false, "You can only cancel your own tutorings.");
        }

        if (tutoring.getStatus() == TutoringStatus.canceled) {
            return new ActionResponseDto(false, "Tutoring is already canceled.");
        }

        if (tutoring.getStatus() == TutoringStatus.completed) {
            return new ActionResponseDto(false, "Cannot cancel a completed tutoring.");
        }

        // Extract needed info before closing session
        int tutorId = tutoring.getTutor().getId();
        String subjectName = tutoring.getSubject().getName();
        LocalDate meetingDate = tutoring.getMeetingDate();
        LocalTime meetingTime = tutoring.getMeetingTime();

        // Update status to canceled
        tutoringRepository.updateStatus(tutoringId, TutoringStatus.canceled);

        // Notify tutor (using extracted data)
        String notificationMessage = String.format(
                "Tutoring session for %s on %s at %s has been canceled by the student",
                subjectName,
                meetingDate,
                meetingTime);
        notificationService.addNotification(tutorId, notificationMessage, "TUTORING_CANCELED");

        return new ActionResponseDto(true, "Tutoring canceled successfully.");
    }

    /**
     * Marks a tutoring as completed.
     * Can only be done after the session date/time has passed.
     * Only confirmed tutorings can be marked as completed.
     *
     * @param studentId ID of the student
     * @param tutoringId ID of the tutoring to complete
     * @return ActionResponseDto indicating success or failure
     */
    public ActionResponseDto completeTutoring(int studentId, int tutoringId) {
        // Load tutoring with all relationships
        Tutoring tutoring;
        try {
            tutoring = tutoringRepository.findByIdWithDetails(tutoringId);
        } catch (Exception e) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getStudent().getId() != studentId) {
            return new ActionResponseDto(false, "You can only complete your own tutorings.");
        }

        if (tutoring.getStatus() != TutoringStatus.confirmed) {
            return new ActionResponseDto(false, "Can only complete confirmed tutorings.");
        }

        // Check if session time has passed
        LocalDateTime sessionDateTime = LocalDateTime.of(tutoring.getMeetingDate(), tutoring.getMeetingTime());
        if (LocalDateTime.now().isBefore(sessionDateTime)) {
            return new ActionResponseDto(false, "Cannot mark as completed before the session time.");
        }

        // Extract needed info before closing session
        int tutorId = tutoring.getTutor().getId();
        String subjectName = tutoring.getSubject().getName();
        LocalDate meetingDate = tutoring.getMeetingDate();

        // Update status to completed
        tutoringRepository.updateStatus(tutoringId, TutoringStatus.completed);

        // Notify tutor (using extracted data)
        String notificationMessage = String.format(
                "Tutoring session for %s on %s has been marked as completed",
                subjectName,
                meetingDate);
        notificationService.addNotification(tutorId, notificationMessage, "TUTORING_COMPLETED");

        return new ActionResponseDto(true, "Tutoring marked as completed.");
    }

    /**
     * Updates a tutoring session.
     * Can update date, time, and topic.
     * Cannot update completed or canceled sessions.
     * Cannot update sessions that have already passed.
     * If date/time changes, status returns to unconfirmed.
     *
     * @param studentId ID of the student
     * @param dto Data transfer object with update details
     * @return ActionResponseDto indicating success or failure
     */
    public ActionResponseDto updateTutoring(int studentId, UpdateTutoringRequestDto dto) {
        // Load tutoring with all relationships
        Tutoring tutoring;
        try {
            tutoring = tutoringRepository.findByIdWithDetails(dto.tutoringId());
        } catch (Exception e) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getStudent().getId() != studentId) {
            return new ActionResponseDto(false, "You can only update your own tutorings.");
        }

        if (tutoring.getStatus() == TutoringStatus.completed) {
            return new ActionResponseDto(false, "Cannot update a completed tutoring.");
        }

        if (tutoring.getStatus() == TutoringStatus.canceled) {
            return new ActionResponseDto(false, "Cannot update a canceled tutoring.");
        }

        // Check if session has already passed
        LocalDateTime sessionDateTime = LocalDateTime.of(tutoring.getMeetingDate(), tutoring.getMeetingTime());
        if (LocalDateTime.now().isAfter(sessionDateTime)) {
            return new ActionResponseDto(false, "Cannot update a tutoring that has already passed.");
        }

        // Extract current values
        LocalDate currentDate = tutoring.getMeetingDate();
        LocalTime currentTime = tutoring.getMeetingTime();
        int tutorId = tutoring.getTutor().getId();
        String subjectName = tutoring.getSubject().getName();

        // Determine new date and time
        LocalDate newDate = dto.meetingDate() != null ? dto.meetingDate() : currentDate;
        LocalTime newTime = dto.meetingTime() != null ? dto.meetingTime() : currentTime;

        // Validate new date is not in the past
        if (newDate.isBefore(LocalDate.now())) {
            return new ActionResponseDto(false, "Cannot schedule tutoring for past dates.");
        }

        // Check for schedule conflicts (excluding current tutoring)
        if (tutoringRepository.hasScheduleConflictExcluding(
                tutorId,
                newDate,
                newTime,
                dto.tutoringId())) {
            return new ActionResponseDto(false, "Tutor already has a session at the new time.");
        }

        // Update tutoring details
        tutoringRepository.updateTutoringDetails(dto.tutoringId(), newDate, newTime, dto.topicName());

        // If date or time changed, reset status to unconfirmed
        if (!newDate.equals(currentDate) || !newTime.equals(currentTime)) {
            tutoringRepository.updateStatus(dto.tutoringId(), TutoringStatus.unconfirmed);
        }

        // Notify tutor (using extracted data)
        String notificationMessage = String.format(
                "Tutoring session for %s has been updated. New schedule: %s at %s",
                subjectName,
                newDate,
                newTime);
        notificationService.addNotification(tutorId, notificationMessage, "TUTORING_UPDATED");

        return new ActionResponseDto(true, "Tutoring updated successfully. Waiting for tutor confirmation.");
    }
}
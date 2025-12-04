package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.NotificationRepository;
import cli.tutoeasy.repository.TutorRepository;
import cli.tutoeasy.repository.TutoringRepository;
import cli.tutoeasy.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Service class for handling tutor-related operations.
 * This class provides methods for creating tutors, managing tutoring requests,
 * and other tutor-specific functionalities.
 * </p>
 *
 * <p>
 * It uses {@link UserRepository}, {@link TutorRepository}, and
 * {@link TutoringRepository} to interact with the database and {@link NotificationService}
 * to send notifications to users.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 * @see TutorRepository
 * @see TutoringRepository
 * @see NotificationService
 */
public class TutorService {

    /**
     * The repository for managing user data.
     */
    private final UserRepository userRepository;
    /**
     * The repository for managing tutor data.
     */
    private final TutorRepository tutorRepository;
    /**
     * The repository for managing tutoring data.
     */
    private final TutoringRepository tutoringRepository;
    /**
     * The service for managing notifications.
     */
    private final NotificationService notificationService;

    /**
     * Constructs a new instance of the {@code TutorService}.
     *
     * @param userRepository      The repository for managing user data.
     * @param tutorRepository     The repository for managing tutor data.
     * @param tutoringRepository  The repository for managing tutoring data.
     * @param notificationService The service for managing notifications.
     */
    public TutorService(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            TutoringRepository tutoringRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.tutoringRepository = tutoringRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new tutor account.
     *
     * <p>
     * This method checks if a user with the provided email already exists.
     * If not, it creates a new user with the 'tutor' role and saves it to the database.
     * </p>
     *
     * @param dto The data transfer object containing the new tutor's information (name, email, password).
     * @return An {@link ActionResponseDto} indicating the result of the operation (success or failure message).
     */
    public ActionResponseDto createTutor(CreateTutorDto dto) {

        User existing = userRepository.findByEmail(dto.email());
        if (existing != null) {
            return new ActionResponseDto(false, "The tutor is already registered with that email.");
        }

        User tutor = new User();
        tutor.setUsername(dto.name());
        tutor.setEmail(dto.email());
        tutor.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        tutor.setRol(UserRole.tutor);

        userRepository.save(tutor);

        return new ActionResponseDto(true, "Tutor successfully registered.");
    }

    /**
     * Retrieves a list of pending tutoring requests for a specific tutor.
     *
     * <p>
     * This method fetches all tutoring sessions that are currently pending approval
     * for the given tutor ID.
     * </p>
     *
     * @param tutorId The unique identifier of the tutor.
     * @return A list of {@link TutorTutoringRequestDto} objects representing the pending requests.
     * @throws IllegalArgumentException if the user with the given ID is not found or is not a tutor.
     */
    public List<TutorTutoringRequestDto> getPending(int tutorId) {

        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null)
            throw new IllegalArgumentException("User is not a tutor.");

        List<Tutoring> list = tutoringRepository.findPendingByTutor(tutorId);

        return list.stream()
                .map(t -> new TutorTutoringRequestDto(
                        t.getId(),
                        t.getStudent().getId(),
                        t.getStudent().getUsername(),
                        t.getSubject().getName(),
                        t.getTopic() != null
                                ? t.getTopic().getName()
                                : "No topic",
                        t.getMeetingDate(),
                        t.getMeetingTime()))
                .toList();
    }

    /**
     * Accepts a pending tutoring request.
     *
     * <p>
     * This method confirms a tutoring session. It verifies that the request belongs to the tutor,
     * is in the 'unconfirmed' state, and does not conflict with existing schedules.
     * If successful, it updates the status to 'confirmed' and sends a notification to the student.
     * </p>
     *
     * @param tutorId    The unique identifier of the tutor accepting the request.
     * @param tutoringId The unique identifier of the tutoring request to accept.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto accept(int tutorId, int tutoringId) {
        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null)
            return new ActionResponseDto(false, "You are not a tutor.");

        var tutoring = tutoringRepository.findByIdWithDetails(tutoringId);
        if (tutoring == null || tutoring.getTutor().getId() != tutorId)
            return new ActionResponseDto(false, "Tutoring not found or not yours.");

        if (tutoring.getStatus() != TutoringStatus.unconfirmed)
            return new ActionResponseDto(false, "This tutoring is not pending.");

        if (tutoringRepository.hasScheduleConflict(
                tutorId,
                tutoring.getMeetingDate(),
                tutoring.getMeetingTime())) {
            return new ActionResponseDto(false, "Schedule conflict detected.");
        }

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.confirmed);

        var studentId = tutoring.getStudent().getId();
        String message = String.format(
                "Your tutoring request for %s on %s at %s has been confirmed by %s",
                tutoring.getSubject().getName(),
                tutoring.getMeetingDate(),
                tutoring.getMeetingTime(),
                tutor.getUsername());
        notificationService.addNotification( studentId, message,"TUTORING_CONFIRMED");

        return new ActionResponseDto(true, "Tutoring confirmed.");
    }

    /**
     * Rejects a pending tutoring request.
     *
     * <p>
     * This method rejects a tutoring session. It verifies that the request belongs to the tutor
     * and is in the 'unconfirmed' state.
     * If successful, it updates the status to 'canceled' and sends a notification to the student.
     * </p>
     *
     * @param tutorId    The unique identifier of the tutor rejecting the request.
     * @param tutoringId The unique identifier of the tutoring request to reject.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto reject(int tutorId, int tutoringId) {
        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null)
            return new ActionResponseDto(false, "You are not a tutor.");

        var tutoring = tutoringRepository.findByIdWithDetails(tutoringId);
        if (tutoring == null || tutoring.getTutor().getId() != tutorId)
            return new ActionResponseDto(false, "Tutoring not found or not yours.");

        if (tutoring.getStatus() != TutoringStatus.unconfirmed)
            return new ActionResponseDto(false, "This tutoring is not pending.");

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.canceled);

        var studentId = tutoring.getStudent().getId();
        String message = String.format(
                "Your tutoring request for %s on %s at %s has been rejected by %s",
                tutoring.getSubject().getName(),
                tutoring.getMeetingDate(),
                tutoring.getMeetingTime(),
                tutor.getUsername());
        notificationService.addNotification( studentId, message,"TUTORING_CONFIRMED");


        return new ActionResponseDto(true, "Tutoring rejected.");
    }

    /**
     * Retrieves all confirmed upcoming sessions for a tutor.
     *
     * <p>
     * This method fetches all tutoring sessions that are confirmed and scheduled for the future
     * for the given tutor ID.
     * </p>
     *
     * @param tutorId The unique identifier of the tutor.
     * @return A list of {@link TutorTutoringDto} objects representing the upcoming sessions.
     */
    public List<TutorTutoringDto> getUpcomingSessions(int tutorId) {
        List<Tutoring> sessions = tutoringRepository.findUpcomingByTutor(tutorId);

        return sessions.stream()
                .map(t -> new TutorTutoringDto(
                        t.getId(),
                        t.getStudent().getUsername(),
                        t.getSubject().getName(),
                        t.getTopic() != null ? t.getTopic().getName() : null,
                        t.getMeetingDate(),
                        t.getMeetingTime(),
                        t.getStatus().name()))
                .collect(Collectors.toList());
    }

    /**
     * Cancels a confirmed tutoring session.
     *
     * <p>
     * This method allows a tutor to cancel a previously confirmed session.
     * It validates that the session is confirmed and not already completed.
     * Upon cancellation, the student is notified.
     * </p>
     *
     * @param tutorId    The unique identifier of the tutor canceling the session.
     * @param tutoringId The unique identifier of the tutoring session to cancel.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto cancelTutoring(int tutorId, int tutoringId) {
        var tutoring = tutoringRepository.findByIdWithDetails(tutoringId);

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getTutor().getId() != tutorId) {
            return new ActionResponseDto(false, "You can only cancel your own tutoring sessions.");
        }

        if (tutoring.getStatus() != TutoringStatus.confirmed) {
            return new ActionResponseDto(false, "Can only cancel confirmed sessions.");
        }

        if (tutoring.getStatus() == TutoringStatus.completed) {
            return new ActionResponseDto(false, "Cannot cancel a completed tutoring.");
        }

        int studentId = tutoring.getStudent().getId();
        String subjectName = tutoring.getSubject().getName();
        LocalDate meetingDate = tutoring.getMeetingDate();
        LocalTime meetingTime = tutoring.getMeetingTime();

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.canceled);

        String message = String.format(
                "Your tutoring session for %s on %s at %s has been canceled by the tutor",
                subjectName,
                meetingDate,
                meetingTime);
        notificationService.addNotification(studentId, message, "TUTORING_CANCELED");

        return new ActionResponseDto(true, "Tutoring session canceled successfully.");
    }

    /**
     * Marks a tutoring session as completed.
     *
     * <p>
     * This method allows a tutor to mark a confirmed session as completed after it has taken place.
     * It validates that the session time has passed before allowing completion.
     * Upon completion, the student is notified.
     * </p>
     *
     * @param tutorId    The unique identifier of the tutor completing the session.
     * @param tutoringId The unique identifier of the tutoring session to complete.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto completeTutoring(int tutorId, int tutoringId) {
        var tutoring = tutoringRepository.findByIdWithDetails(tutoringId);

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getTutor().getId() != tutorId) {
            return new ActionResponseDto(false, "You can only complete your own tutoring sessions.");
        }

        if (tutoring.getStatus() != TutoringStatus.confirmed) {
            return new ActionResponseDto(false, "Can only complete confirmed tutorings.");
        }

        LocalDateTime sessionDateTime = LocalDateTime.of(tutoring.getMeetingDate(), tutoring.getMeetingTime());
        if (LocalDateTime.now().isBefore(sessionDateTime)) {
            return new ActionResponseDto(false, "Cannot mark as completed before the session time.");
        }

        int studentId = tutoring.getStudent().getId();
        String subjectName = tutoring.getSubject().getName();
        LocalDate meetingDate = tutoring.getMeetingDate();

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.completed);

        String message = String.format(
                "Your tutoring session for %s on %s has been marked as completed by the tutor",
                subjectName,
                meetingDate);
        notificationService.addNotification(studentId, message, "TUTORING_COMPLETED");

        return new ActionResponseDto(true, "Tutoring marked as completed.");
    }

    /**
     * Updates the schedule of a confirmed tutoring session.
     *
     * <p>
     * This method allows a tutor to reschedule a confirmed session (date and/or time).
     * It validates that the session is confirmed, has not passed, and that the new schedule
     * does not conflict with other sessions.
     * The student is notified of the update.
     * </p>
     *
     * @param tutorId The unique identifier of the tutor updating the session.
     * @param dto     The data transfer object containing the updated schedule details (new date/time).
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto updateTutoring(int tutorId, UpdateTutoringDto dto) {
        var tutoring = tutoringRepository.findByIdWithDetails(dto.tutoringId());

        if (tutoring == null) {
            return new ActionResponseDto(false, "Tutoring not found.");
        }

        if (tutoring.getTutor().getId() != tutorId) {
            return new ActionResponseDto(false, "You can only update your own tutoring sessions.");
        }

        if (tutoring.getStatus() != TutoringStatus.confirmed) {
            return new ActionResponseDto(false, "Can only update confirmed sessions.");
        }

        if (tutoring.getStatus() == TutoringStatus.completed) {
            return new ActionResponseDto(false, "Cannot update a completed tutoring.");
        }

        LocalDateTime sessionDateTime = LocalDateTime.of(tutoring.getMeetingDate(), tutoring.getMeetingTime());
        if (LocalDateTime.now().isAfter(sessionDateTime)) {
            return new ActionResponseDto(false, "Cannot update a tutoring that has already passed.");
        }

        LocalDate currentDate = tutoring.getMeetingDate();
        LocalTime currentTime = tutoring.getMeetingTime();
        int studentId = tutoring.getStudent().getId();
        String subjectName = tutoring.getSubject().getName();

        LocalDate newDate = dto.meetingDate() != null ? dto.meetingDate() : currentDate;
        LocalTime newTime = dto.meetingTime() != null ? dto.meetingTime() : currentTime;

        if (newDate.isBefore(LocalDate.now())) {
            return new ActionResponseDto(false, "Cannot schedule tutoring for past dates.");
        }

        if (tutoringRepository.hasScheduleConflictExcluding(
                tutorId,
                newDate,
                newTime,
                dto.tutoringId())) {
            return new ActionResponseDto(false, "You already have a session at the new time.");
        }

        tutoringRepository.updateTutoringDetails(dto.tutoringId(), newDate, newTime, null);

        String message = String.format(
                "Your tutoring session for %s has been rescheduled by the tutor. New schedule: %s at %s",
                subjectName,
                newDate,
                newTime);
        notificationService.addNotification(studentId, message, "TUTORING_UPDATED");

        return new ActionResponseDto(true, "Tutoring session updated successfully. Student has been notified.");
    }
}
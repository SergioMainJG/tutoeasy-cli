package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.*;
import cli.tutoeasy.repository.*;

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
     * Repository for managing subjects in the database.
     */
    private final SubjectRepository subjectRepository;

    /**
     * Repository for managing the tutor's expertise (subjects linked to tutors).
     */
    private final TutorExpertiseRepository expertiseRepository;

    /**
     * Repository for managing tutors' schedules.
     */
    private final TutorScheduleRepository scheduleRepository;

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
            NotificationService notificationService,
            SubjectRepository subjectRepository,
            TutorExpertiseRepository expertiseRepository,
            TutorScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.tutoringRepository = tutoringRepository;
        this.notificationService = notificationService;
        this.subjectRepository = subjectRepository;
        this.expertiseRepository = expertiseRepository;
        this.scheduleRepository = scheduleRepository;
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

    /**
     * Updates a tutor's profile including subjects and schedule.
     *
     * <p>
     * This method performs the following operations:
     * </p>
     * <ol>
     *     <li>Validates that the given user ID belongs to a tutor.</li>
     *     <li>Creates any new subjects provided in the DTO if they do not exist.</li>
     *     <li>Deletes all existing tutor expertise and adds the new list of subjects.</li>
     *     <li>Deletes the current schedule and adds the new schedule slots provided in the DTO.</li>
     * </ol>
     *
     * @param dto The data transfer object containing the tutor ID, subjects, and schedule slots.
     * @return An {@link ActionResponseDto} indicating success or failure of the operation.
     * @throws IllegalArgumentException if any schedule slot has an invalid day.
     */
    public ActionResponseDto editTutorProfile(EditTutorProfileDto dto) {
        User tutor = userRepository.findById(dto.tutorId());
        if (tutor == null || tutor.getRol() != UserRole.tutor)
            return new ActionResponseDto(false, "Invalid tutor.");

        // Subjects
        List<Subject> subjects = dto.subjectNames().stream()
                .map(name -> {
                    Subject s = subjectRepository.findByName(name);
                    if (s == null) {
                        s = new Subject();
                        s.setName(name);
                        subjectRepository.save(s);
                    }
                    return s;
                })
                .toList();

        expertiseRepository.deleteByTutor(dto.tutorId());
        for (Subject s : subjects) {
            expertiseRepository.addSubjectToTutor(dto.tutorId(), s.getId());
        }

        // Schedule
        scheduleRepository.deleteByTutor(dto.tutorId());
        // Combinar todos los slots en un solo string separado por coma, si vienen en líneas separadas
        String combinedSlots = String.join(",", dto.scheduleSlots());
        String[] slots = combinedSlots.split("\\s*,\\s*"); // separa por coma, eliminando espacios

        for (String slot : slots) {
            String[] parts = slot.split("-");
            if (parts.length != 3) continue; // ignorar si no tiene formato correcto
            TutorSchedule ts = new TutorSchedule();
            ts.setTutor(tutor);
            ts.setDayOfWeek(convertDayToNumber(parts[0].trim())); // convertDayToNumber hace .toLowerCase()
            ts.setStartTime(LocalTime.parse(parts[1].trim()));
            ts.setEndTime(LocalTime.parse(parts[2].trim()));
            scheduleRepository.save(ts);
        }

        return new ActionResponseDto(true, "Tutor profile updated successfully.");
    }
    /**
     * Converts a day of the week from its name to a numeric representation.
     *
     * <p>
     * Monday = 1, Tuesday = 2, ..., Sunday = 7.
     * </p>
     *
     * @param day The name of the day (case-insensitive).
     * @return The numeric representation of the day.
     * @throws IllegalArgumentException if the day name is invalid.
     */
    private int convertDayToNumber(String day) {
        return switch (day.toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            case "sunday" -> 7;
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };
    }
    /**
     * Retrieves all completed tutoring sessions for a given tutor.
     *
     * <p>This method fetches sessions marked as completed for the specified tutor
     * and maps them to a list of {@link TutorTutoringRequestDto}.</p>
     *
     * @param tutorId the ID of the tutor
     * @return a list of completed tutoring session DTOs
     * @throws IllegalArgumentException if the user is not found or not a tutor
     */
    public List<TutorTutoringRequestDto> getCompletedTutorings(int tutorId) {
        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            throw new IllegalArgumentException("User is not a tutor.");
        }

        List<Tutoring> sessions = tutoringRepository.findCompletedByTutor(tutorId);

        return sessions.stream()
                .map(t -> new TutorTutoringRequestDto(
                        t.getId(),
                        t.getStudent().getId(),
                        t.getStudent().getUsername(),
                        t.getSubject().getName(),
                        t.getTopic() != null ? t.getTopic().getName() : "No topic",
                        t.getMeetingDate(),
                        t.getMeetingTime()
                ))
                .toList();
    }
}
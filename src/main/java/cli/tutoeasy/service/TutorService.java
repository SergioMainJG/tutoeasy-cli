package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.TutorRepository;
import cli.tutoeasy.repository.TutoringRepository;
import cli.tutoeasy.repository.UserRepository;

import java.util.List;

/**
 * Service class for handling tutor-related operations.
 * This class provides methods for creating tutors, managing tutoring requests, and other tutor-specific functionalities.
 * It uses {@link UserRepository}, {@link TutorRepository}, and {@link TutoringRepository} to interact with the database.
 *
 * @see UserRepository
 * @see TutorRepository
 * @see TutoringRepository
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
     * Constructs a new instance of the {@code TutorService}.
     *
     * @param userRepository     The repository for managing user data.
     * @param tutorRepository    The repository for managing tutor data.
     * @param tutoringRepository The repository for managing tutoring data.
     */
    public TutorService(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            TutoringRepository tutoringRepository
    ) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.tutoringRepository = tutoringRepository;
    }

    /**
     * Creates a new tutor.
     *
     * @param dto The data transfer object containing the tutor's information.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
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
     * Retrieves a list of pending tutoring requests for a given tutor.
     *
     * @param tutorId The ID of the tutor.
     * @return A list of {@link TutorTutoringRequestDto} objects representing the pending requests.
     * @throws IllegalArgumentException if the user is not a tutor.
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
                        t.getMeetingTime()
                ))
                .toList();
    }

    /**
     * Accepts a tutoring request.
     *
     * @param tutorId    The ID of the tutor.
     * @param tutoringId The ID of the tutoring request to accept.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto accept(int tutorId, int tutoringId) {

        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null)
            return new ActionResponseDto(false, "You are not a tutor.");

        var tutoring = tutoringRepository.findById(tutoringId);
        if (tutoring == null || tutoring.getTutor().getId() != tutorId)
            return new ActionResponseDto(false, "Tutoring not found or not yours.");

        if (tutoring.getStatus() != TutoringStatus.unconfirmed)
            return new ActionResponseDto(false, "This tutoring is not pending.");

        if (tutoringRepository.hasScheduleConflict(
                tutorId,
                tutoring.getMeetingDate(),
                tutoring.getMeetingTime()
        )) {
            return new ActionResponseDto(false, "Schedule conflict detected.");
        }

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.confirmed);

        return new ActionResponseDto(true, "Tutoring confirmed.");
    }

    /**
     * Rejects a tutoring request.
     *
     * @param tutorId    The ID of the tutor.
     * @param tutoringId The ID of the tutoring request to reject.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto reject(int tutorId, int tutoringId) {

        var tutor = tutorRepository.findById(tutorId);
        if (tutor == null)
            return new ActionResponseDto(false, "You are not a tutor.");

        var tutoring = tutoringRepository.findById(tutoringId);
        if (tutoring == null || tutoring.getTutor().getId() != tutorId)
            return new ActionResponseDto(false, "Tutoring not found or not yours.");

        if (tutoring.getStatus() != TutoringStatus.unconfirmed)
            return new ActionResponseDto(false, "This tutoring is not pending.");

        tutoringRepository.updateStatus(tutoringId, TutoringStatus.canceled);

        return new ActionResponseDto(true, "Tutoring canceled.");
    }
}
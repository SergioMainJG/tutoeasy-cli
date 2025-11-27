package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.TutorRepository;
import cli.tutoeasy.repository.TutoringRepository;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.util.AuthSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TutorService {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final TutoringRepository tutoringRepository;

    public TutorService(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            TutoringRepository tutoringRepository
    ) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.tutoringRepository = tutoringRepository;
    }

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
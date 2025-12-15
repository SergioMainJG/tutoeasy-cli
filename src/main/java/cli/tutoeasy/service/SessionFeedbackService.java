package cli.tutoeasy.service;

import cli.tutoeasy.model.dto.CreateSessionFeedbackDto;
import cli.tutoeasy.model.dto.CreateTutorFeedbackDto;
import cli.tutoeasy.model.entities.SessionFeedback;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;
import cli.tutoeasy.repository.SessionFeedbackRepository;
import cli.tutoeasy.repository.TutoringRepository;

/**
 * Service responsible for managing feedback for tutoring sessions.
 *
 * <p>
 * Handles creation of feedback left by students or tutors after completing a session.
 * Enforces business rules such as ownership validation, session status, and duplicate prevention.
 * </p>
 */
public class SessionFeedbackService {

    private final SessionFeedbackRepository feedbackRepository;
    private final TutoringRepository tutoringRepository;

    public SessionFeedbackService(SessionFeedbackRepository feedbackRepository,
                                  TutoringRepository tutoringRepository) {
        this.feedbackRepository = feedbackRepository;
        this.tutoringRepository = tutoringRepository;
    }

    /**
     * Adds feedback from a student using a DTO.
     */
    public void addFeedback(CreateSessionFeedbackDto dto) {
        Tutoring tutoring = tutoringRepository.findByIdWithDetails(dto.tutoringId());
        if (tutoring == null)
            throw new IllegalArgumentException("Tutoring session not found.");

        if (tutoring.getStudent().getId() != dto.studentId())
            throw new IllegalStateException("The tutoring does not belong to this student.");

        if (tutoring.getStatus() != TutoringStatus.completed)
            throw new IllegalStateException("Only completed tutorials can be evaluated.");

        if (feedbackRepository.existsByTutoringId(dto.tutoringId()))
            throw new IllegalStateException("This tutoring session has already been evaluated.");

        SessionFeedback feedback = new SessionFeedback();
        feedback.setTutoringId(dto.tutoringId());
        feedback.setStudentId(dto.studentId());
        feedback.setTutorId(tutoring.getTutor().getId());
        feedback.setRating(dto.rating());
        feedback.setComment(dto.comment());
        feedback.setCreatedAt(dto.createdAt());

        feedbackRepository.save(feedback);
    }

    /**
     * Adds feedback from a tutor about a student using a DTO.
     */
    public void addTutorFeedback(CreateTutorFeedbackDto dto) {
        Tutoring tutoring = tutoringRepository.findByIdWithDetails(dto.tutoringId());
        if (tutoring == null)
            throw new IllegalArgumentException("Tutoring session not found.");

        if (tutoring.getTutor().getId() != dto.tutorId())
            throw new IllegalStateException("This tutoring session does not belong to this tutor.");

        if (tutoring.getStatus() != TutoringStatus.completed)
            throw new IllegalStateException("Only completed tutorials can be evaluated.");

        if (feedbackRepository.existsTutorFeedback(dto.tutoringId()))
            throw new IllegalStateException("You have already evaluated this student.");

        SessionFeedback feedback = new SessionFeedback();
        feedback.setTutoringId(dto.tutoringId());
        feedback.setStudentId(tutoring.getStudent().getId());
        feedback.setTutorId(dto.tutorId());
        feedback.setRating(dto.rating());
        feedback.setComment(dto.comment());
        feedback.setCreatedAt(dto.createdAt());
        feedback.setTutorObservation(true);

        feedbackRepository.save(feedback);
    }
    
}

package cli.tutoeasy.service;

import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.repository.TutoringRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for automatically sending notifications to tutors.
 * <p>
 * This service is responsible for:
 * <ul>
 *     <li>Periodically checking upcoming tutoring sessions for tutors.</li>
 *     <li>Sending automatic notifications to tutors 1 day and 30 minutes before the session.</li>
 * </ul>
 * <p>
 * It works without modifying the Notification table or adding additional columns.
 */
public class TutorNotificationScheduler {

    private final TutoringRepository tutoringRepository;
    private final NotificationService notificationService;

    /**
     * Constructor that initializes the scheduler and starts periodic execution.
     *
     * @param tutoringRepository Repository to access tutors' sessions.
     * @param notificationService Service to send notifications to tutors.
     */
    public TutorNotificationScheduler(TutoringRepository tutoringRepository,
                                      NotificationService notificationService) {
        this.tutoringRepository = tutoringRepository;
        this.notificationService = notificationService;
        startScheduler();
    }

    /**
     * Initializes a {@link ScheduledExecutorService} that executes
     * {@link #checkUpcomingSessions()} every minute.
     */
    private void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkUpcomingSessions, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Checks upcoming sessions for each tutor and sends automatic notifications.
     * <p>
     * Sends:
     * <ul>
     *     <li>A reminder 1 day before the session.</li>
     *     <li>A reminder 30 minutes before the session.</li>
     * </ul>
     * <p>
     * Notifications are only sent if one of the corresponding type has not been sent yet.
     */
    private void checkUpcomingSessions() {
        List<Integer> tutorIds = tutoringRepository.getAllTutorIds();
        LocalDateTime now = LocalDateTime.now();

        for (int tutorId : tutorIds) {
            List<Tutoring> upcoming = tutoringRepository.findUpcomingByTutor(tutorId);

            for (Tutoring t : upcoming) {
                LocalDateTime sessionDateTime = LocalDateTime.of(t.getMeetingDate(), t.getMeetingTime());

                // Reminder 1 day before
                if (!notificationAlreadySent(t.getTutor().getId(), "reminder_1day") &&
                        sessionDateTime.minusDays(1).isBefore(now)) {
                    notificationService.notifyTutorForSession(t, "reminder_1day");
                }

                // Reminder 30 minutes before
                if (!notificationAlreadySent(t.getTutor().getId(), "reminder_30min") &&
                        sessionDateTime.minusMinutes(30).isBefore(now)) {
                    notificationService.notifyTutorForSession(t, "reminder_30min");
                }
            }
        }
    }

    /**
     * Checks if a reminder of a specific type has already been sent to a tutor.
     *
     * @param userId Tutor's ID.
     * @param type Notification type ("reminder_1day" or "reminder_30min").
     * @return {@code true} if a notification of that type already exists, {@code false} otherwise.
     */
    private boolean notificationAlreadySent(int userId, String type) {
        return notificationService.getNotifications(userId).stream()
                .anyMatch(n -> type.equals(n.getType()));
    }
}


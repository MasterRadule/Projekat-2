package ktsnvt.tim1.services;

import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.ReservableSeatGroup;
import ktsnvt.tim1.model.Seat;
import ktsnvt.tim1.model.VerificationToken;
import ktsnvt.tim1.repositories.ReservationRepository;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PeriodicCheckService {

    @Autowired
    private EmailService emailService;

    @Value("${notifyHoursBeforeExpiration}")
    private double notifyHoursBeforeExpiration;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationService reservationService;

    @Scheduled(cron = "${checkReservations.cron}")
    public void checkReservations() {
        reservationRepository.findByOrderIdIsNullAndIsCancelledFalse().forEach((reservation) ->
                {
                    LocalDateTime firstEventDay =
                            reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(LocalDateTime::compareTo)
                                    .get();
                    LocalDateTime expirationDate =
                            firstEventDay.minusDays(reservation.getEvent().getReservationDeadlineDays());
                    if (expirationDate.isBefore(LocalDateTime.now())) {
                        reservationService.cancelReservationRemoveConnections(reservation);
                        reservationRepository.save(reservation);
                        emailService.sendReservationExpiredEmail(reservation.getRegisteredUser(), reservation, expirationDate);
                    } else if (expiresSoon(expirationDate)) {
                        emailService
                                .sendReservationNotificationEmail(reservation.getRegisteredUser(), reservation, expirationDate);
                    }
                }
        );
    }

    @Scheduled(cron = "${checkToken.cron}")
    public void checkToken() {
        userRepository.findByIsVerifiedFalse().forEach((user) -> {
            VerificationToken vt = verificationTokenRepository.findByUser(user);
            if (vt.isExpired()) {
                verificationTokenRepository.delete(vt);
                userRepository.delete(user);
            }

        });
    }

    private boolean expiresSoon(LocalDateTime expirationDate) {
        long numOfHoursBeforeExpiration = ChronoUnit.HOURS.between(LocalDateTime.now(), expirationDate);
        return numOfHoursBeforeExpiration < notifyHoursBeforeExpiration;
    }
}

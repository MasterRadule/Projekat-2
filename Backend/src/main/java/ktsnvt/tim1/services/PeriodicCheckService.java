package ktsnvt.tim1.services;

import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.Reservation;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class PeriodicCheckService {

    @Autowired
    private EmailService emailService;

    @Value("${notifyHoursBeforeExpiration}")
    private double notifyHoursBeforeExpiration;

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(cron = "${checkReservations.cron}")
    public void checkReservations() {
        reservationRepository.findByOrderIdIsNullAndIsCancelledFalse().forEach((reservation) ->
                {
                    Date firstEventDay = reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
                    long reservationDeadlineDaysInMilliseconds = TimeUnit.MILLISECONDS.convert(reservation.getEvent().getReservationDeadlineDays(), TimeUnit.DAYS);
                    Date expirationDate = new Date();
                    expirationDate.setTime(firstEventDay.getTime() - reservationDeadlineDaysInMilliseconds);
                    if (expirationDate.before(new Date())) {
                        reservation.setCancelled(true);
                        reservationRepository.save(reservation);
                        emailService.sendReservationExpiredEmail(reservation.getRegisteredUser(), reservation, expirationDate);
                    } else if (expiresSoon(expirationDate)) {
                        emailService.sendReservationNotificationEmail(reservation.getRegisteredUser(), reservation, expirationDate);
                    }
                }
        );
    }

    private boolean expiresSoon(Date expirationDate) {
        int numOfHoursBeforeExpiration = (int) TimeUnit.HOURS.convert(Math.abs(expirationDate.getTime()
                - new Date().getTime()), TimeUnit.MILLISECONDS);
        return numOfHoursBeforeExpiration < notifyHoursBeforeExpiration;
    }
}

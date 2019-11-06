package ktsnvt.tim1.services;

import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.Reservation;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class PeriodicCheckService {

    @Value("${notifyHoursBeforeExpiration}")
    private double notifyHoursBeforeExpiration;

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(cron = "${checkReservations.cron}")
    public void checkReservations() {
        reservationRepository.findByOrderIdIsNullAndIsCancelledFalse().forEach((reservation) ->
                {
                    if (expiresSoon(reservation)) sendMail(reservation);
                }
        );
    }

    private boolean expiresSoon(Reservation reservation) {
        Date firstEventDay = reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
        long reservationDeadlineDaysInMilliseconds = TimeUnit.MILLISECONDS.convert(reservation.getEvent().getReservationDeadlineDays(), TimeUnit.DAYS);
        int numOfHoursBeforeExpiration = (int) TimeUnit.HOURS.convert(Math.abs(firstEventDay.getTime()
                - reservationDeadlineDaysInMilliseconds - new Date().getTime()), TimeUnit.MILLISECONDS);
        return numOfHoursBeforeExpiration < notifyHoursBeforeExpiration;
    }
}

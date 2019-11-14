package ktsnvt.tim1.services;

import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.Reservation;
import ktsnvt.tim1.model.VerificationToken;
import ktsnvt.tim1.repositories.ReservationRepository;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
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

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "${checkReservations.cron}")
    public void checkReservations() {
        reservationRepository.findByOrderIdIsNullAndIsCancelledFalse().forEach((reservation) ->
                {
                    Date firstEventDay = reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
                    long reservationDeadlineDaysInMilliseconds = TimeUnit.MILLISECONDS.convert(reservation.getEvent().getReservationDeadlineDays(), TimeUnit.DAYS);
                    Date expirationDate = new Date();
                    expirationDate.setTime(firstEventDay.getTime() - reservationDeadlineDaysInMilliseconds);
                    if (expiresSoon(reservation, expirationDate)) {
                        RegisteredUser registeredUser = reservation.getRegisteredUser();
                        emailService.sendReservationNotificationEmail(registeredUser, reservation, expirationDate);
                    }
                }
        );
    }

    @Scheduled(cron = "${checkToken.cron}")
    public void checkToken(){
        userRepository.findByEmailNotNullAndIsVerifiedFalse().forEach((user)->{
            VerificationToken vt = verificationTokenRepository.findByUser(user);
            if(vt.isExpired()){
                verificationTokenRepository.delete(vt);
                userRepository.delete(user);
            }

        });
    }

    private boolean expiresSoon(Reservation reservation, Date expirationDate) {
        int numOfHoursBeforeExpiration = (int) TimeUnit.HOURS.convert(Math.abs(expirationDate.getTime()
                - new Date().getTime()), TimeUnit.MILLISECONDS);
        return numOfHoursBeforeExpiration < notifyHoursBeforeExpiration;
    }
}

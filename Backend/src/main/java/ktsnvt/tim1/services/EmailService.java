package ktsnvt.tim1.services;

import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.Reservation;
import ktsnvt.tim1.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Value("${date.format}")
    private String dateFormat;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Async
    public void sendReservationNotificationEmail(RegisteredUser registeredUser, Reservation reservation, Date expirationDate) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        mailMessage.setTo(registeredUser.getEmail());
        mailMessage.setSubject("Reservation expires soon");
        mailMessage.setFrom(emailAddress);
        mailMessage.setText(String.format("Dear %s %s,\nWe want to inform you that your reservation for event %s is about to expire (expires on %s).\nKTSNVT",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(), sdf.format(expirationDate)));
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendVerificationEmail(RegisteredUser registeredUser, String url, VerificationToken verificationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registeredUser.getEmail());
        mailMessage.setSubject("Complete Registration");
        mailMessage.setFrom(emailAddress);
        mailMessage.setText(String.format("Dear %s %s,\nTo confirm your account please click here: \n"
                +"http://"+url+"/api/verify-account?token="+verificationToken.getToken(),registeredUser.getFirstName(), registeredUser.getLastName()));
        javaMailSender.send(mailMessage);

    }
}

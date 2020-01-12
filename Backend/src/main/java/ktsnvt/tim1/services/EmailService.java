package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ReservationDTO;
import ktsnvt.tim1.DTOs.TicketDTO;
import ktsnvt.tim1.mappers.ReservationMapper;
import ktsnvt.tim1.model.*;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.stream.Collectors.joining;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Value("${dateTime.format}")
    private String dateTimeFormat;

    @Value("${date.format}")
    private String dateFormat;


    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendReservationNotificationEmail(RegisteredUser registeredUser, Reservation reservation,
                                                 LocalDateTime expirationDate) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        mailMessage.setTo(registeredUser.getEmail());
        mailMessage.setSubject("Reservation expires soon");
        mailMessage.setFrom(emailAddress);
        mailMessage.setText(String.format("Dear %s %s,\nWe want to inform you that your reservation for event %s is about to expire (expires on %s).\nKTSNVT",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)));
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendReservationExpiredEmail(RegisteredUser registeredUser, Reservation reservation,
                                            LocalDateTime expirationDate) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        mailMessage.setTo(registeredUser.getEmail());
        mailMessage.setSubject("Reservation expired");
        mailMessage.setFrom(emailAddress);
        mailMessage.setText(String.format("Dear %s %s,\nWe want to inform you that your reservation for event %s has expired at %s.\nKTSNVT",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)));
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendVerificationEmail(RegisteredUser registeredUser, String url, VerificationToken verificationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registeredUser.getEmail());
        mailMessage.setSubject("Complete registration");
        mailMessage.setFrom(emailAddress);
        mailMessage.setText(String.format("Dear %s %s,\nTo confirm your account please click here: \n"
                + "http://" + url + "/api/verify-account?token=" + verificationToken.getToken(), registeredUser.getFirstName(), registeredUser.getLastName()));
        javaMailSender.send(mailMessage);

    }

    @Async
    public void sendReservationBoughtEmail(Reservation reservation) throws MessagingException {

        RegisteredUser registeredUser = reservation.getRegisteredUser();
        ReservationDTO reservationDTO = reservationMapper.toDTO(reservation);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(registeredUser.getEmail());
        helper.setSubject("Reservation bought");
        helper.setFrom(emailAddress);

        StringBuilder text = new StringBuilder("<html><body>");
        text.append(String.format("Dear %s %s,<br>" +
                        "You successfully bought the reservation for event %s.<br>" +
                        "Here are your tickets:<br>",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservationDTO.getEventName()));

        for (TicketDTO ticketDTO : reservationDTO.getTickets()) {
            text.append(String.format("<div style='border-style:solid;margin:40px;padding:30px;'>" +
                            "<b>Event: </b>%s<br>" +
                            "<b>Seat group: </b>%s<br>" +
                            "<b>Seat row: </b>%s<br>" +
                            "<b>Seat column: </b>%s<br>" +
                            "<b>Dates: </b>%s<br>" +
                            "<b>QR code: </b><br><img src='cid:%s'>",
                    reservationDTO.getEventName(),
                    ticketDTO.getSeatGroupName(),
                    ticketDTO.getRowNum() == null ? "/" : ticketDTO.getRowNum().toString(),
                    ticketDTO.getColNum() == null ? "/" : ticketDTO.getColNum().toString(),
                    ticketDTO.getEventDays().stream().map(date -> date.format(formatter)).collect(joining(", ")),
                    ticketDTO.getId().toString()
            ));
            text.append("</div>");
        }
        text.append("</body></html>");
        helper.setText(text.toString(), true);
        for (TicketDTO t : reservationDTO.getTickets()) {
            helper.addInline(t.getId().toString(), QRCode.from(reservation.getId().toString()).file());
        }
        javaMailSender.send(mimeMessage);
    }
}

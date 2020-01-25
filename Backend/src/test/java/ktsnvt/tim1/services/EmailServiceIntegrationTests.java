package ktsnvt.tim1.services;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import ktsnvt.tim1.model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EmailServiceIntegrationTests {

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Value("${dateTime.format}")
    private String dateTimeFormat;

    @Value("${date.format}")
    private String dateFormat;


    private static GreenMail greenMail;

    @Autowired
    public EmailService emailService;

    @BeforeAll
    public static void setupSMTP() {
        greenMail = new GreenMail(new ServerSetup(2525, "localhost", "smtp"));
        greenMail.setUser("ktsnvt", "ktsnvttim1");
        greenMail.start();
    }

    @AfterAll
    public static void stopSMTP() {
        greenMail.stop();
    }

    @Test
    public void sendReservationNotificationEmail() throws MessagingException, IOException {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        Reservation reservation = new Reservation();
        Event event = new Event();
        event.setName("eventName");
        reservation.setEvent(event);
        LocalDateTime expirationDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        emailService.sendReservationNotificationEmail(registeredUser, reservation, expirationDate);

        assertTrue(greenMail.waitForIncomingEmail(5000, greenMail.getReceivedMessages().length + 1));
        MimeMessage mimeMessage = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];

        assertEquals(registeredUser.getEmail(), mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(emailAddress, mimeMessage.getFrom()[0].toString());
        assertEquals("Reservation expires soon", mimeMessage.getSubject());
        assertEquals(String.format("Dear %s %s,%nWe want to inform you that your reservation for event %s is about to expire (expires on %s). %nKTSNVT%n",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)), mimeMessage.getContent().toString());
    }

    @Test
    public void sendReservationExpiredEmail() throws MessagingException, IOException {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        Reservation reservation = new Reservation();
        Event event = new Event();
        event.setName("eventName");
        reservation.setEvent(event);
        LocalDateTime expirationDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        emailService.sendReservationExpiredEmail(registeredUser, reservation, expirationDate);

        assertTrue(greenMail.waitForIncomingEmail(5000, greenMail.getReceivedMessages().length + 1));
        MimeMessage mimeMessage = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];

        assertEquals(registeredUser.getEmail(), mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(emailAddress, mimeMessage.getFrom()[0].toString());
        assertEquals("Reservation expired", mimeMessage.getSubject());
        assertEquals(String.format("Dear %s %s,%nWe want to inform you that your reservation for event %s has expired at %s.%nKTSNVT%n",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)), mimeMessage.getContent().toString());
    }

    @Test
    public void sendVerificationEmail() throws MessagingException, IOException{
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        String url = "www.ktsnvt.com";
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("token123");

        emailService.sendVerificationEmail(registeredUser, url, verificationToken);

        assertTrue(greenMail.waitForIncomingEmail(5000, greenMail.getReceivedMessages().length + 1));
        MimeMessage mimeMessage = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];

        assertEquals(registeredUser.getEmail(), mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(emailAddress, mimeMessage.getFrom()[0].toString());
        assertEquals("Complete registration", mimeMessage.getSubject());
        assertEquals(String.format("Dear %s %s,%nTo confirm your account please click here: %n"
                + "http://" + url + "/api/verify-account?token=" + verificationToken.getToken() + "%n",
                registeredUser.getFirstName(), registeredUser.getLastName()), mimeMessage.getContent().toString());
    }

    @Test
    public void sendReservationBoughtEmail() throws MessagingException, IOException {
        Reservation reservation = new Reservation();
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        reservation.setRegisteredUser(registeredUser);
        Event event = new Event();
        event.setName("eventName");
        reservation.setEvent(event);
        Seat seat = new Seat();
        seat.setColNum(1);
        seat.setRowNum(1);
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        EventSeatGroup esg = new EventSeatGroup();
        rsg.setEventSeatGroup(esg);
        SeatGroup sg = new SeatGroup();
        sg.setName("SeatGroupName");
        esg.setSeatGroup(sg);
        EventDay ed = new EventDay();
        ed.setDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        ed.setEvent(event);
        event.getEventDays().add(ed);
        rsg.setEventDay(ed);
        ed.getReservableSeatGroups().add(rsg);
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        reservation.getTickets().add(ticket);
        ticket.setReservation(reservation);
        seat.setTicket(ticket);
        ticket.getSeats().add(seat);
        rsg.getTickets().add(ticket);
        ticket.getReservableSeatGroups().add(rsg);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        emailService.sendReservationBoughtEmail(reservation);

        assertTrue(greenMail.waitForIncomingEmail(5000, greenMail.getReceivedMessages().length + 1));
        MimeMessage mimeMessage = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];

        assertEquals(registeredUser.getEmail(), mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(emailAddress, mimeMessage.getFrom()[0].toString());
        assertEquals("Reservation bought", mimeMessage.getSubject());
        assertEquals(String.format("<html><body>Dear %s %s,<br>" +
                        "You successfully bought the reservation for event %s.<br>" +
                        "Here are your tickets:<br>" +
                        "<div style='border-style:solid;margin:40px;padding:30px;'>" +
                        "<b>Event: </b>%s<br>" +
                        "<b>Seat group: </b>%s<br>" +
                        "<b>Seat row: </b>%s<br>" +
                        "<b>Seat column: </b>%s<br>" +
                        "<b>Dates: </b>%s<br>" +
                        "<b>QR code: </b><br><img src='cid:%s'></div></body></html>",
                registeredUser.getFirstName(), registeredUser.getLastName(), event.getName(),
                event.getName(),
                sg.getName(),
                seat.getRowNum(),
                seat.getColNum(),
                ed.getDate().format(formatter),
                ticket.getId()), getHtmlFromMimeMessage(mimeMessage));
    }

    public static String getHtmlFromMimeMessage(MimeMessage mimeMessage) throws MessagingException, IOException {
        MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
        mimeMultipart = (MimeMultipart) mimeMultipart.getBodyPart(0).getContent();
        return mimeMultipart.getBodyPart(0).getContent().toString();
    }
}

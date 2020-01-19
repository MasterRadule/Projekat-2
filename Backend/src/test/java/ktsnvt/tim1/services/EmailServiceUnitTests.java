package ktsnvt.tim1.services;

import ktsnvt.tim1.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class EmailServiceUnitTests {

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Value("${dateTime.format}")
    private String dateTimeFormat;

    @Value("${date.format}")
    private String dateFormat;


    @Autowired
    private EmailService emailService;

    @SpyBean
    private JavaMailSender javaMailSenderSpy;

    @BeforeEach
    public void mockJavaMailSenderSend() {
        Mockito.doNothing().when(javaMailSenderSpy).send((MimeMessage) ArgumentMatchers.any());
        Mockito.doNothing().when(javaMailSenderSpy).send((SimpleMailMessage) ArgumentMatchers.any());
    }

    @Test
    public void sendReservationNotificationEmail() {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        Reservation reservation = new Reservation();
        Event event = new Event();
        event.setName("eventName");
        reservation.setEvent(event);
        LocalDateTime expirationDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        emailService.sendReservationNotificationEmail(registeredUser, reservation, expirationDate);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(javaMailSenderSpy, Mockito.timeout(1000)).send(captor.capture());
        assertEquals(registeredUser.getEmail(), captor.getValue().getTo()[0]);
        assertEquals("Reservation expires soon", captor.getValue().getSubject());
        assertEquals(emailAddress, captor.getValue().getFrom());
        assertEquals(String.format("Dear %s %s,%nWe want to inform you that your reservation for event %s is about to expire (expires on %s). %nKTSNVT",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)), captor.getValue().getText());
    }

    @Test
    public void sendReservationExpiredEmail() {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail("mail@example.com");
        Reservation reservation = new Reservation();
        Event event = new Event();
        event.setName("eventName");
        reservation.setEvent(event);
        LocalDateTime expirationDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        emailService.sendReservationExpiredEmail(registeredUser, reservation, expirationDate);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(javaMailSenderSpy, Mockito.timeout(1000)).send(captor.capture());
        assertEquals(registeredUser.getEmail(), captor.getValue().getTo()[0]);
        assertEquals("Reservation expired", captor.getValue().getSubject());
        assertEquals(emailAddress, captor.getValue().getFrom());
        assertEquals(captor.getValue().getText(), String.format("Dear %s %s,%nWe want to inform you that your reservation for event %s has expired at %s.%nKTSNVT",
                registeredUser.getFirstName(), registeredUser.getLastName(), reservation.getEvent().getName(),
                formatter.format(expirationDate)));
    }

    @Test
    public void sendVerificationEmail() {
        //TODO
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

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(javaMailSenderSpy, Mockito.timeout(1000)).send(captor.capture());

        assertEquals(registeredUser.getEmail(), captor.getValue().getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(emailAddress, captor.getValue().getFrom()[0].toString());
        assertEquals("Reservation bought", captor.getValue().getSubject());
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
                ticket.getId()), EmailServiceIntegrationTests.getHtmlFromMimeMessage(captor.getValue()));
    }
}

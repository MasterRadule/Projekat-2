package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.NewReservationDTO;
import ktsnvt.tim1.DTOs.NewTicketDTO;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.ReservationRepository;

import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore("javax.security.*")
@PrepareForTest({PeriodicCheckService.class})
@ActiveProfiles("test")
public class PeriodicCheckServiceUnitTests {

    @Value("${notifyHoursBeforeExpiration}")
    private double notifyHoursBeforeExpiration;

    @Autowired
    private PeriodicCheckService periodicCheckService;

    @MockBean
    private ReservationRepository reservationRepositoryMocked;

    @MockBean
    private ReservationService reservationServiceMocked;

    @MockBean
    private EmailService emailServiceMocked;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private VerificationTokenRepository verificationTokenRepositoryMocked;

    @Test
    public void checkReservations_reservationDeadlinePassed_cancelReservationRemoveConnectionsCalledAndMailSent() {
        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(1))); // tomorrow 00:00
        event.setReservationDeadlineDays(2);
        RegisteredUser registeredUser = new RegisteredUser();
        Reservation reservation = new Reservation(1L, null, false, registeredUser, event);
        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        LocalDateTime firstEventDay =
                reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(LocalDateTime::compareTo)
                        .get();
        LocalDateTime expirationDate =
                firstEventDay.minusDays(reservation.getEvent().getReservationDeadlineDays());

        Mockito.when(reservationRepositoryMocked.findByOrderIdIsNullAndIsCancelledFalse()).thenReturn(reservations);
        periodicCheckService.checkReservations();
        Mockito.verify(reservationServiceMocked, Mockito.times(1)).cancelReservationRemoveConnections(reservation);
        Mockito.verify(emailServiceMocked, Mockito.times(1)).sendReservationExpiredEmail(registeredUser, reservation, expirationDate);
    }

    @Test
    public void checkReservations_reservationExpiresSoon_MailSent() throws Exception {
        PeriodicCheckService periodicCheckServiceSpy = PowerMockito.spy(periodicCheckService);

        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        Integer deadlineDays = 2;
        event.getEventDays().add(new EventDay(1L, LocalDateTime.now().plusHours((long) (deadlineDays * 24 + notifyHoursBeforeExpiration/2))));
        event.setReservationDeadlineDays(deadlineDays);
        RegisteredUser registeredUser = new RegisteredUser();
        Reservation reservation = new Reservation(1L, null, false, registeredUser, event);
        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        LocalDateTime firstEventDay =
                reservation.getEvent().getEventDays().stream().map(EventDay::getDate).min(LocalDateTime::compareTo)
                        .get();
        LocalDateTime expirationDate =
                firstEventDay.minusDays(reservation.getEvent().getReservationDeadlineDays());

        Mockito.when(reservationRepositoryMocked.findByOrderIdIsNullAndIsCancelledFalse()).thenReturn(reservations);
        periodicCheckServiceSpy.checkReservations();
        Mockito.verify(emailServiceMocked, Mockito.times(1)).sendReservationNotificationEmail(registeredUser, reservation, expirationDate);
        PowerMockito.verifyPrivate(periodicCheckServiceSpy, times(1)).invoke("expiresSoon", expirationDate);
    }

    @Test
    public void expiresSoon_doesExpireSoon_returnTrue () throws Exception {
        assertTrue(Whitebox.invokeMethod(periodicCheckService, "expiresSoon",
                LocalDateTime.now().plusHours((long) (notifyHoursBeforeExpiration/2))));
    }

    @Test
    public void expiresSoon_doesNotExpireSoon_returnFalse () throws Exception {
        assertFalse(Whitebox.invokeMethod(periodicCheckService, "expiresSoon",
                LocalDateTime.now().plusHours((long) (notifyHoursBeforeExpiration*1.5))));
    }

    @Test
    public void checkToken_tokenExpired_deleteToken(){
        String token = "123546";
        String email = "ppetrovic@gmail.com";
        User user = new User(1L, "Petar", "Petrovic","$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6",email,false);
        VerificationToken verificationToken = new VerificationToken(1L, token, LocalDateTime.now().minusDays(2), user);
        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        Mockito.when(userRepositoryMocked.findByIsVerifiedFalse()).thenReturn(users);
        Mockito.when(verificationTokenRepositoryMocked.findByUser(user)).thenReturn(verificationToken);
        periodicCheckService.checkToken();
        Mockito.verify(verificationTokenRepositoryMocked, Mockito.times(1)).delete(verificationToken);
        Mockito.verify(userRepositoryMocked, Mockito.times(1)).delete(user);
    }
}
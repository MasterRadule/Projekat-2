package ktsnvt.tim1.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.exceptions.PayPalException;
import ktsnvt.tim1.mappers.ReservationMapper;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.ReservableSeatGroupRepository;
import ktsnvt.tim1.repositories.ReservationRepository;
import ktsnvt.tim1.repositories.SeatRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore("javax.security.*")
@PrepareForTest({ReservationService.class, Payment.class})
@ActiveProfiles("test")
public class ReservationServiceUnitTests {

    @Autowired
    private ReservationService reservationServiceProxy;

    private ReservationService reservationService;

    @MockBean
    private ReservationRepository reservationRepositoryMocked;

    @MockBean
    private EventRepository eventRepositoryMocked;

    @MockBean
    private ReservationMapper reservationMapperMocked;

    @MockBean
    private SeatRepository seatRepositoryMocked;

    @MockBean
    private ReservableSeatGroupRepository reservableSeatGroupRepositoryMocked;

    @MockBean
    private EmailService emailServiceMocked;


    private void setUpPrincipal(RegisteredUser registeredUser) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(registeredUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Before
    public void unwrapReservationService() throws Exception {
        reservationService = (ReservationService) ((Advised) reservationServiceProxy).getTargetSource().getTarget();
    }

    @Test
    public void getReservations_parameterALL_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        Mockito.when(reservationRepositoryMocked.findByRegisteredUserIdAndIsCancelledFalse(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        reservationService.getReservations(ReservationTypeDTO.ALL, pageable);

        verify(reservationRepositoryMocked, times(1)).findByRegisteredUserIdAndIsCancelledFalse(registeredUserId, pageable);
    }

    @Test
    public void getReservations_parameterBOUGHT_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        Mockito.when(reservationRepositoryMocked.findByRegisteredUserIdAndOrderIdIsNotNullAndIsCancelledFalse(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        reservationService.getReservations(ReservationTypeDTO.BOUGHT, pageable);

        verify(reservationRepositoryMocked, times(1)).findByRegisteredUserIdAndOrderIdIsNotNullAndIsCancelledFalse(registeredUserId, pageable);
    }

    @Test
    public void getReservations_parameterRESERVED_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        Mockito.when(reservationRepositoryMocked.findByRegisteredUserIdAndOrderIdIsNullAndIsCancelledFalse(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        reservationService.getReservations(ReservationTypeDTO.RESERVED, pageable);

        verify(reservationRepositoryMocked, times(1))
                .findByRegisteredUserIdAndOrderIdIsNullAndIsCancelledFalse(registeredUserId, pageable);
    }

    @Test
    public void getReservation_reservationExists_reservationReturned() throws EntityNotFoundException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        ReservationDTO reservationDTO = reservationService.getReservation(reservationId);

        assertEquals(reservationId, reservationDTO.getId());
        verify(reservationRepositoryMocked, times(1)).findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUserId);
    }

    @Test
    public void getReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Optional<Reservation> o = Optional.empty();
        Mockito.when(reservationRepositoryMocked.findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUserId)).thenReturn(o);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.getReservation(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }


    @Test
    public void createReservation_makeReservationObjectCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long eventId = 1L;
        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(eventId, tickets);


        PowerMockito.doReturn(null).when(reservationServiceSpy, "makeReservationObject", newReservationDTO, true);
        reservationServiceSpy.createReservation(newReservationDTO);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makeReservationObject", newReservationDTO, true);
    }

    @Test
    public void makeReservationObject_noSuchEvent_entityNotFoundException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(10000L, tickets);

        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                Whitebox.invokeMethod(reservationService, "makeReservationObject", newReservationDTO, true));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    public void makeReservationObject_tooManyTickets_entityNotValidException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Event event = new Event();
        event.setMaxTicketsPerReservation(1);

        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.of(event));

        Exception exception = assertThrows(EntityNotValidException.class, () ->
                Whitebox.invokeMethod(reservationService, "makeReservationObject", newReservationDTO, true));

        assertEquals("Too many tickets in the reservation", exception.getMessage());
    }

    @Test
    public void makeReservationObject_eventStarted_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDateTime.of(2018, 1, 1, 0, 0)));

        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.of(event));

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "makeReservationObject", newReservationDTO, true));

        assertEquals("Event already started", exception.getMessage());
    }

    @Test
    public void makeReservationObject_eventHasNoEventDays_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Event event = new Event();
        event.setMaxTicketsPerReservation(5);


        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.of(event));

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "makeReservationObject", newReservationDTO, true));

        assertEquals("Event does not have event days", exception.getMessage());
    }

    @Test
    public void makeReservationObject_reservationDeadlinePassed_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(1))); // tomorrow 00:00
        event.setReservationDeadlineDays(2);

        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.of(event));

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "makeReservationObject", newReservationDTO, true));

        assertEquals("Reservation deadline date passed", exception.getMessage());
    }

    @Test
    public void makeReservationObject_makeTicketObjectCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);

        PowerMockito.doReturn(new Ticket()).when(reservationServiceSpy, "makeTicketObject", any(), any());
        Mockito.when(eventRepositoryMocked.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId()))
                .thenReturn(Optional.of(event));

        Whitebox.invokeMethod(reservationServiceSpy, "makeReservationObject", newReservationDTO, true);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(3)).invoke("makeTicketObject", any(), any());
    }

    @Test
    public void makeTicketObject_singleDaySeatTicket_reserveSeatSingleDayCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 12L, false);
        Event event = new Event();
        event.setId(1L);

        PowerMockito.doNothing().when(reservationServiceSpy, "reserveSeatSingleDay", any(), any(), any());

        Whitebox.invokeMethod(reservationServiceSpy, "makeTicketObject", ticketDTO, event);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("reserveSeatSingleDay", any(), any(), any());
    }

    @Test
    public void makeTicketObject_allDaySeatTicket_reserveSeatAllDaysCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 12L, true);
        Event event = new Event();
        event.setId(1L);

        PowerMockito.doNothing().when(reservationServiceSpy, "reserveSeatAllDays", any(), any(), any());

        Whitebox.invokeMethod(reservationServiceSpy, "makeTicketObject", ticketDTO, event);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("reserveSeatAllDays", any(), any(), any());
    }


    @Test
    public void makeTicketObject_parterreSingleDayTicket_reserveParterreSingleDayCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        NewTicketDTO ticketDTO = new NewTicketDTO(2L, null, false);
        Event event = new Event();
        event.setId(1L);

        PowerMockito.doNothing().when(reservationServiceSpy, "reserveParterreSingleDay", any(), any(), any());

        Whitebox.invokeMethod(reservationServiceSpy, "makeTicketObject", ticketDTO, event);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("reserveParterreSingleDay", any(), any(), any());
    }

    @Test
    public void makeTicketObject_parterreAllDayTicket_reserveParterreAllDaysCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        NewTicketDTO ticketDTO = new NewTicketDTO(2L, null, true);
        Event event = new Event();
        event.setId(1L);

        PowerMockito.doNothing().when(reservationServiceSpy, "reserveParterreAllDays", any(), any(), any());

        Whitebox.invokeMethod(reservationServiceSpy, "makeTicketObject", ticketDTO, event);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("reserveParterreAllDays", any(), any(), any());
    }

    @Test
    public void reserveSeatSingleDay_seatDoesNotExist_entityNotFoundException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, false);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveSeatSingleDay", ticket, event, ticketDTO));
        assertEquals("Seat not found", exception.getMessage());
    }

    @Test
    public void reserveSeatSingleDay_seatTaken_impossibleActionException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, false);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();
        Seat seat = new Seat();
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        seat.setReservableSeatGroup(rsg);
        seat.setTicket(new Ticket());

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.of(seat));

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveSeatSingleDay", ticket, event, ticketDTO));
        assertEquals("Seat is already taken", exception.getMessage());
    }

    @Test
    public void reserveSeatSingleDay_everythingValid_ticketConnected() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, false);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();
        Seat seat = new Seat();
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        int freeSeats = 1;
        rsg.setFreeSeats(freeSeats);
        seat.setReservableSeatGroup(rsg);

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.of(seat));
        Whitebox.invokeMethod(reservationService, "reserveSeatSingleDay", ticket, event, ticketDTO);

        assertEquals(1, ticket.getReservableSeatGroups().size());
        assertEquals(rsg, ticket.getReservableSeatGroups().iterator().next());
        assertEquals(1, ticket.getSeats().size());
        assertEquals(seat, ticket.getSeats().iterator().next());

        assertTrue(rsg.getTickets().contains(ticket));
        assertEquals(ticket, seat.getTicket());

        assertEquals(Integer.valueOf(freeSeats - 1), rsg.getFreeSeats());
    }

    @Test
    public void reserveSeatAllDays_seatDoesNotExist_entityNotFoundException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, true);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveSeatAllDays", ticket, event, ticketDTO));
        assertEquals("Seat not found", exception.getMessage());
    }

    @Test
    public void reserveSeatAllDays_seatNotFreeAllDays_impossibleActionException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, true);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        EventSeatGroup esg = new EventSeatGroup(1L);
        Ticket ticket = new Ticket();
        int freeSeats = 3;
        ReservableSeatGroup rsg1 = new ReservableSeatGroup();
        rsg1.setEventSeatGroup(esg);
        rsg1.setFreeSeats(freeSeats);
        Seat seat1 = new Seat(1, 1, rsg1);
        seat1.setTicket(null);
        ReservableSeatGroup rsg2 = new ReservableSeatGroup();
        rsg2.setEventSeatGroup(esg);
        rsg2.setFreeSeats(freeSeats);
        Seat seat2 = new Seat(1, 1, rsg2);
        seat2.setTicket(new Ticket());
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(seat1);
        seats.add(seat2);

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.of(seat1));
        Mockito.when(seatRepositoryMocked.getSeatsByRowNumAndColNum(event.getId(), rsg1.getEventSeatGroup().getId(), seat1.getRowNum(), seat1.getColNum()))
                .thenReturn(seats);

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveSeatAllDays", ticket, event, ticketDTO));

        assertEquals("Seat is not free for all days", exception.getMessage());
    }

    @Test
    public void reserveSeatAllDays_everythingValid_ticketConnected() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, true);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        EventSeatGroup esg = new EventSeatGroup(1L);
        Ticket ticket = new Ticket();
        int freeSeats = 3;
        ReservableSeatGroup rsg1 = new ReservableSeatGroup();
        rsg1.setEventSeatGroup(esg);
        rsg1.setFreeSeats(freeSeats);
        Seat seat1 = new Seat(1, 1, rsg1);
        seat1.setTicket(null);
        ReservableSeatGroup rsg2 = new ReservableSeatGroup();
        rsg2.setEventSeatGroup(esg);
        rsg2.setFreeSeats(freeSeats);
        Seat seat2 = new Seat(1, 1, rsg2);
        seat2.setTicket(null);
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(seat1);
        seats.add(seat2);

        Mockito.when(seatRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getSeatId())).thenReturn(Optional.of(seat1));
        Mockito.when(seatRepositoryMocked.getSeatsByRowNumAndColNum(event.getId(), rsg1.getEventSeatGroup().getId(), seat1.getRowNum(), seat1.getColNum()))
                .thenReturn(seats);

        Whitebox.invokeMethod(reservationService, "reserveSeatAllDays", ticket, event, ticketDTO);


        assertEquals(2, ticket.getReservableSeatGroups().size());
        assertTrue(ticket.getReservableSeatGroups().contains(rsg1));
        assertTrue(ticket.getReservableSeatGroups().contains(rsg2));
        assertEquals(2, ticket.getSeats().size());
        assertTrue(ticket.getSeats().contains(seat1));
        assertTrue(ticket.getSeats().contains(seat2));

        assertTrue(rsg1.getTickets().contains(ticket));
        assertTrue(rsg2.getTickets().contains(ticket));
        assertEquals(ticket, seat1.getTicket());
        assertEquals(ticket, seat2.getTicket());

        assertEquals(Integer.valueOf(freeSeats - 1), rsg1.getFreeSeats());
        assertEquals(Integer.valueOf(freeSeats - 1), rsg2.getFreeSeats());
    }

    @Test
    public void reserveParterreSingleDay_parterreDoesNotExist_entityNotFoundException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, null, false);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();

        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveParterreSingleDay", ticket, event, ticketDTO));
        assertEquals("Parterre not found", exception.getMessage());
    }

    @Test
    public void reserveParterreSingleDay_parterreFull_impossibleActionException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, false);
        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        rsg.setFreeSeats(0);


        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.of(rsg));

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveParterreSingleDay", ticket, event, ticketDTO));
        assertEquals("Parterre is already fully taken", exception.getMessage());
    }

    @Test
    public void reserveParterreSingleDay_everythingValid_ticketConnected() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, false);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        int freeSeats = 1;
        rsg.setFreeSeats(freeSeats);


        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.of(rsg));

        Whitebox.invokeMethod(reservationService, "reserveParterreSingleDay", ticket, event, ticketDTO);

        assertEquals(1, ticket.getReservableSeatGroups().size());
        assertEquals(rsg, ticket.getReservableSeatGroups().iterator().next());

        assertTrue(rsg.getTickets().contains(ticket));

        assertEquals(Integer.valueOf(freeSeats - 1), rsg.getFreeSeats());
    }

    @Test
    public void reserveParterreAllDays_parterreDoesNotExist_entityNotFoundException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, null, true);
        Event event = new Event();
        event.setId(1L);
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        Ticket ticket = new Ticket();

        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveParterreAllDays", ticket, event, ticketDTO));
        assertEquals("Parterre not found", exception.getMessage());
    }

    @Test
    public void reserveParterreAllDays_parterreNotFreeAllDays_impossibleActionException() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, true);
        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        EventSeatGroup esg = new EventSeatGroup(1L);
        Ticket ticket = new Ticket();
        ReservableSeatGroup rsg1 = new ReservableSeatGroup();
        rsg1.setFreeSeats(1);
        rsg1.setEventSeatGroup(esg);
        ReservableSeatGroup rsg2 = new ReservableSeatGroup();
        rsg2.setFreeSeats(0);
        rsg2.setEventSeatGroup(esg);
        ArrayList<ReservableSeatGroup> rsgs = new ArrayList<>();
        rsgs.add(rsg1);
        rsgs.add(rsg2);

        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.of(rsg1));
        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndByEventSeatGroup(event.getId(), esg.getId()))
                .thenReturn(rsgs);

        Exception exception = assertThrows(ImpossibleActionException.class, () ->
                Whitebox.invokeMethod(reservationService, "reserveParterreAllDays", ticket, event, ticketDTO));
        assertEquals("Parterre not free for all days", exception.getMessage());
    }

    @Test
    public void reserveParterreAllDays_everythingValid_ticketConnected() throws Exception {
        NewTicketDTO ticketDTO = new NewTicketDTO(2L, 10L, true);
        Event event = new Event();
        event.setMaxTicketsPerReservation(5);
        event.getEventDays().add(new EventDay(1L, LocalDate.now().atTime(0, 0).plusDays(5)));
        event.setReservationDeadlineDays(2);
        EventSeatGroup esg = new EventSeatGroup(1L);
        Ticket ticket = new Ticket();
        int freeSeats = 1;
        ReservableSeatGroup rsg1 = new ReservableSeatGroup();
        rsg1.setFreeSeats(freeSeats);
        rsg1.setEventSeatGroup(esg);
        ReservableSeatGroup rsg2 = new ReservableSeatGroup();
        rsg2.setFreeSeats(freeSeats);
        rsg2.setEventSeatGroup(esg);
        ArrayList<ReservableSeatGroup> rsgs = new ArrayList<>();
        rsgs.add(rsg1);
        rsgs.add(rsg2);


        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId()))
                .thenReturn(Optional.of(rsg1));
        Mockito.when(reservableSeatGroupRepositoryMocked.findByEventAndByEventSeatGroup(event.getId(), esg.getId()))
                .thenReturn(rsgs);

        Whitebox.invokeMethod(reservationService, "reserveParterreAllDays", ticket, event, ticketDTO);

        assertEquals(2, ticket.getReservableSeatGroups().size());
        assertTrue(ticket.getReservableSeatGroups().contains(rsg1));
        assertTrue(ticket.getReservableSeatGroups().contains(rsg2));

        assertTrue(rsg1.getTickets().contains(ticket));
        assertTrue(rsg2.getTickets().contains(ticket));

        assertEquals(Integer.valueOf(freeSeats - 1), rsg1.getFreeSeats());
        assertEquals(Integer.valueOf(freeSeats - 1), rsg2.getFreeSeats());
    }

    @Test
    public void cancelReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Optional<Reservation> o = Optional.empty();
        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.cancelReservation(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    public void cancelReservation_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, "orderId", false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.cancelReservation(reservationId));
        assertEquals("Reservation is already paid, therefore cannot be cancelled", exception.getMessage());
    }

    @Test
    public void cancelReservation_everythingValid_reservationCanceled() throws ImpossibleActionException, EntityNotFoundException {
        ReservationService reservationServiceSpy = Mockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationRepositoryMocked.save(entity)).thenReturn(entity);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);

        ReservationDTO reservationDTO = reservationServiceSpy.cancelReservation(reservationId);
        Mockito.verify(reservationServiceSpy, times(1)).cancelReservationRemoveConnections(entity);
        assertEquals(true, entity.getCancelled());
        assertEquals(reservationId, reservationDTO.getId());
    }

    @Test
    public void cancelReservationRemoveConnections_everythingValid_reservationCanceledConnectionsRemoved() throws ImpossibleActionException, EntityNotFoundException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation reservation = new Reservation(reservationId, null, false, registeredUser, null);
        Ticket ticket = new Ticket();
        reservation.getTickets().add(ticket);
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        rsg.setFreeSeats(1);
        Seat seat = new Seat();

        ticket.getSeats().add(seat);
        seat.setTicket(ticket);
        ticket.getReservableSeatGroups().add(rsg);
        rsg.getTickets().add(ticket);

        reservationService.cancelReservationRemoveConnections(reservation);
        assertEquals(true, reservation.getCancelled());
        assertTrue(reservation.getTickets().contains(ticket));
        assertTrue(ticket.getSeats().isEmpty());
        assertNull(seat.getTicket());
        assertFalse(rsg.getTickets().contains(ticket));
        assertEquals(Integer.valueOf(2), rsg.getFreeSeats());
        assertTrue(ticket.getReservableSeatGroups().isEmpty());
    }

    @Test
    public void payReservationCreatePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Optional<Reservation> o = Optional.empty();
        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.payReservationCreatePayment(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    public void payReservationCreatePayment_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, "orderId", false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.payReservationCreatePayment(reservationId));
        assertEquals("Reservation is already paid, therefore cannot be payed again", exception.getMessage());
    }

    @Test
    public void payReservationCreatePayment_everythingValid_makePaymentObjectCalled() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);
        Payment payment = new Payment();
        payment.setId("paymentId");

        Mockito.when(reservationRepositoryMocked.findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "makePaymentObject", entity);
        reservationServiceSpy.payReservationCreatePayment(reservationId);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makePaymentObject", entity);
    }

    @Test
    public void payReservationExecutePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Optional<Reservation> o = Optional.empty();
        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    public void payReservationExecutePayment_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, "orderId", false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Reservation is already paid, therefore cannot be payed again", exception.getMessage());
    }


    @Test
    public void payReservationExecutePayment_paymentNotForReservation_impossibleActionException() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        PowerMockito.doReturn(true).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationServiceSpy.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Sent payment ID matches the payment which does not correspond to sent reservation", exception.getMessage());
    }

    @Test
    public void payReservationExecutePayment_everythingValidButMailCannotBeSent_paymentExecuted() throws Exception {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        Mockito.doThrow(new MessagingException("mail error")).when(emailServiceMocked).sendReservationBoughtEmail(entity);
        PowerMockito.doReturn(false).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "executePayment", paymentId, payerId);

        reservationServiceSpy.payReservationExecutePayment(reservationId, paymentDTO);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("executePayment", paymentId, payerId);
        assertEquals(paymentId, entity.getOrderId());
        assertEquals("Message not sent because of exception: mail error\r\n", outContent.toString());

        System.setOut(originalOut);
    }

    @Test
    public void payReservationExecutePayment_everythingValid_paymentExecuted() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        PowerMockito.doReturn(false).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "executePayment", paymentId, payerId);

        reservationServiceSpy.payReservationExecutePayment(reservationId, paymentDTO);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("executePayment", paymentId, payerId);
        assertEquals(paymentId, entity.getOrderId());

    }

    @Test
    public void createAndPayReservationCreatePayment_everythingValid_paymentCreated() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        Long eventId = 1L;
        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(eventId, tickets);
        String paymentId = "paymentId";
        Payment payment = new Payment();
        payment.setId(paymentId);

        PowerMockito.doReturn(entity).when(reservationServiceSpy, "makeReservationObject", newReservationDTO, false);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "makePaymentObject", entity);

        Exception exception = assertThrows(NoTransactionException.class, () -> reservationServiceSpy.createAndPayReservationCreatePayment(newReservationDTO));
        assertEquals("No transaction aspect-managed TransactionStatus in scope", exception.getMessage());
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makeReservationObject", newReservationDTO, false);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makePaymentObject", entity);
    }

    @Test
    public void createAndPayReservationExecutePayment_paymentNotForReservation_impossibleActionException() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        Long eventId = 1L;
        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(eventId, tickets);

        PowerMockito.doReturn(entity).when(reservationServiceSpy, "makeReservationObject", newReservationDTO, false);
        PowerMockito.doReturn(true).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationServiceSpy.createAndPayReservationExecutePayment(newReservationDTO, paymentDTO));
        assertEquals("Sent payment ID matches the payment which does not correspond to sent reservation", exception.getMessage());
    }

    @Test
    public void createAndPayReservationExecutePayment_everythingValidMailCannotBeSent_paymentExecuted() throws Exception {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        Long eventId = 1L;
        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(eventId, tickets);

        Mockito.doThrow(new MessagingException("mail error")).when(emailServiceMocked).sendReservationBoughtEmail(entity);
        PowerMockito.doReturn(entity).when(reservationServiceSpy, "makeReservationObject", newReservationDTO, false);
        PowerMockito.doReturn(false).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "executePayment", paymentId, payerId);

        reservationServiceSpy.createAndPayReservationExecutePayment(newReservationDTO, paymentDTO);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makeReservationObject", newReservationDTO, false);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("executePayment", paymentId, payerId);
        assertEquals(paymentId, entity.getOrderId());
        assertEquals("Message not sent because of exception: mail error\r\n", outContent.toString());

        System.setOut(originalOut);
    }

    @Test
    public void createAndPayReservationExecutePayment_everythingValid_paymentExecuted() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);
        Long reservationId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        Long eventId = 1L;
        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(eventId, tickets);

        PowerMockito.doReturn(entity).when(reservationServiceSpy, "makeReservationObject", newReservationDTO, false);
        PowerMockito.doReturn(false).when(reservationServiceSpy, "paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.doReturn(payment).when(reservationServiceSpy, "executePayment", paymentId, payerId);

        reservationServiceSpy.createAndPayReservationExecutePayment(newReservationDTO, paymentDTO);

        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("makeReservationObject", newReservationDTO, false);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("paymentIsNotForReservation", entity, paymentDTO);
        PowerMockito.verifyPrivate(reservationServiceSpy, times(1)).invoke("executePayment", paymentId, payerId);
        assertEquals(paymentId, entity.getOrderId());
    }

    @Test
    public void makePaymentObject_paymentNotCreated_payPalException() {
        Reservation reservation = new Reservation(1L, null, false, new RegisteredUser(), null);
        reservation.setTickets(new HashSet<>());

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setReservation(reservation);
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        ticket.getReservableSeatGroups().add(rsg);
        EventSeatGroup esg = new EventSeatGroup();
        rsg.setEventSeatGroup(esg);
        double ticketPrice = 10;
        esg.setPrice(ticketPrice);

        reservation.getTickets().add(ticket);

        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(null);
        Exception exception = assertThrows(PayPalException.class, () ->
                Whitebox.invokeMethod(reservationService, "makePaymentObject", reservation));
        assertEquals("Payment not created", exception.getMessage());
    }

    @Test
    public void makePaymentObject_everythingValid_paymentCreated() throws Exception {
        Reservation reservation = new Reservation(1L, null, false, new RegisteredUser(), null);
        reservation.setTickets(new HashSet<>());

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setReservation(reservation);
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        ticket.getReservableSeatGroups().add(rsg);
        EventSeatGroup esg = new EventSeatGroup();
        rsg.setEventSeatGroup(esg);
        double ticketPrice = 10;
        esg.setPrice(ticketPrice);

        reservation.getTickets().add(ticket);

        Payment payment = Mockito.spy(Payment.class);
        Mockito.doReturn(payment).when(payment).create((APIContext)any());
        PowerMockito.whenNew(Payment.class)
                .withNoArguments()
                .thenReturn(payment);

        Payment createdPayment = Whitebox.invokeMethod(reservationService, "makePaymentObject", reservation);
        assertEquals(1, createdPayment.getTransactions().size());
        Transaction transaction = createdPayment.getTransactions().get(0);
        assertNotNull(transaction.getItemList());
        ItemList itemList = transaction.getItemList();
        assertEquals(1, itemList.getItems().size());
        Item item = itemList.getItems().get(0);
        assertEquals("KTSNVT - Ticket",item.getDescription());
        assertEquals("TicketID: " + ticket.getId(), item.getName());
        assertEquals("EUR", item.getCurrency());
        assertEquals(String.valueOf(ticketPrice), item.getPrice());
        assertEquals("1", item.getQuantity());

        assertEquals("EUR", transaction.getAmount().getCurrency());
        assertEquals(String.valueOf(ticketPrice), transaction.getAmount().getTotal());
        assertEquals("1", item.getQuantity());

        assertEquals("paypal", createdPayment.getPayer().getPaymentMethod());
        assertEquals("/", createdPayment.getRedirectUrls().getCancelUrl());
        assertEquals("/", createdPayment.getRedirectUrls().getReturnUrl());
        assertEquals("sale", createdPayment.getIntent());
    }

    @Test
    public void paymentIsNotForReservation_paymentGetReturnsNull_PayPalException() throws Exception {
        Reservation reservation = new Reservation(1L, null, false, new RegisteredUser(), null);

        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(null);

        Exception exception = assertThrows(PayPalException.class, () ->
                Whitebox.invokeMethod(reservationService, "paymentIsNotForReservation", reservation, paymentDTO));
        assertEquals("Payment with sent payment ID does not exist", exception.getMessage());
    }

    @Test
    public void paymentIsNotForReservation_paymentIsForReservation_returnFalse() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Reservation reservation = new Reservation(1L, null, false, new RegisteredUser(), null);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Payment payment1 = new Payment();
        Transaction transaction1 = new Transaction();
        ItemList itemList1 = new ItemList();
        ShippingAddress shippingAddress1 = new ShippingAddress();
        itemList1.setShippingAddress(shippingAddress1);
        transaction1.setItemList(itemList1);
        transaction1.setPayee(new Payee());
        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(transaction1);
        payment1.setTransactions(transactions1);

        Payment payment2 = new Payment();
        Transaction transaction2 = new Transaction();
        ItemList itemList2 = new ItemList();
        ShippingAddress shippingAddress2 = new ShippingAddress();
        itemList2.setShippingAddress(shippingAddress2);
        transaction2.setItemList(itemList2);
        transaction2.setPayee(new Payee());
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction2);
        payment2.setTransactions(transactions2);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment1);
        PowerMockito.doReturn(payment2).when(reservationServiceSpy, "makePaymentObject", reservation);

        boolean result = Whitebox.invokeMethod(reservationServiceSpy, "paymentIsNotForReservation", reservation, paymentDTO);
        assertFalse(result);
    }

    @Test
    public void paymentIsNotForReservation_paymentIsForNotReservation_returnTrue() throws Exception {
        ReservationService reservationServiceSpy = PowerMockito.spy(reservationService);

        Reservation reservation = new Reservation(1L, null, false, new RegisteredUser(), null);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Payment payment1 = new Payment();
        Transaction transaction1 = new Transaction();
        ItemList itemList1 = new ItemList();
        ShippingAddress shippingAddress1 = new ShippingAddress();
        itemList1.setShippingAddress(shippingAddress1);
        transaction1.setItemList(itemList1);
        transaction1.setPayee(new Payee());
        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(transaction1);
        payment1.setTransactions(transactions1);

        Payment payment2 = new Payment();
        Transaction transaction2 = new Transaction();
        transaction2.setDescription("description");
        ItemList itemList2 = new ItemList();
        ShippingAddress shippingAddress2 = new ShippingAddress();
        itemList2.setShippingAddress(shippingAddress2);
        transaction2.setItemList(itemList2);
        transaction2.setPayee(new Payee());
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction2);
        payment2.setTransactions(transactions2);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment1);
        PowerMockito.doReturn(payment2).when(reservationServiceSpy, "makePaymentObject", reservation);

        boolean result = Whitebox.invokeMethod(reservationServiceSpy, "paymentIsNotForReservation", reservation, paymentDTO);
        assertTrue(result);
    }

    @Test
    public void executePayment_paymentExecuteReturnsNull_PayPalException() throws Exception {
        String paymentId = "paymentId";
        String payerId = "payerId";

        PowerMockito.stub(PowerMockito.method(Payment.class, "execute", APIContext.class, PaymentExecution.class)).toReturn(null);

        Exception exception = assertThrows(PayPalException.class, () ->
                Whitebox.invokeMethod(reservationService, "executePayment", paymentId, payerId));
        assertEquals("Payment not executed", exception.getMessage());
    }

    @Test
    public void executePayment_everythingValid_paymentExecuted() throws Exception {
        String paymentId = "paymentId";
        String payerId = "payerId";

        Payment payment = Mockito.spy(Payment.class);
        Mockito.doReturn(payment).when(payment).execute((APIContext)any(), any());
        PowerMockito.whenNew(Payment.class)
                .withNoArguments()
                .thenReturn(payment);

        Payment paymentExecuted = Whitebox.invokeMethod(reservationService, "executePayment", paymentId, payerId);
        assertEquals(paymentId, paymentExecuted.getId());
    }
}

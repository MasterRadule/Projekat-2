package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationServiceIntegrationTests {
    @Autowired
    private ReservationService reservationService;


    private void setUpPrincipal(RegisteredUser registeredUser){
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(registeredUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getReservations_parameterALL_allReturned() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ReservationDTO> page = reservationService.getReservations(ReservationTypeDTO.ALL, pageable);

        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getNumberOfElements());
    }

    @Test
    public void getReservations_parameterBOUGHT_boughtReturned() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ReservationDTO> page = reservationService.getReservations(ReservationTypeDTO.BOUGHT, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getNumberOfElements());
    }

    @Test
    public void getReservations_parameterRESERVED_reservedReturned() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ReservationDTO> page = reservationService.getReservations(ReservationTypeDTO.RESERVED, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getNumberOfElements());
    }

    @Test
    public void getReservations_userHasNoUncancelledReservations_emptyReturned() {
        Long registeredUserId = 26L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ReservationDTO> page = reservationService.getReservations(ReservationTypeDTO.ALL, pageable);

        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getNumberOfElements());
    }


    @Test
    public void getReservation_reservationExists_reservationReturned() throws EntityNotFoundException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        ReservationDTO reservationDTO = reservationService.getReservation(reservationId);

        assertEquals(reservationId, reservationDTO.getId());
    }

    @Test
    public void getReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 100L;
        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.getReservation(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_validSeatTicketSingleDay_reservationCreated() throws EntityNotFoundException, ImpossibleActionException, EntityNotValidException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ReservationDTO reservationDTO = reservationService.createReservation(newReservationDTO);

        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertEquals(Integer.valueOf(1), ticketDTO.getColNum());
        assertEquals(Integer.valueOf(2), ticketDTO.getRowNum());
        assertEquals("Side", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(31), ticketDTO.getPrice());
        assertEquals(1, ticketDTO.getEventDays().size());
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 1,0,0), ticketDTO.getEventDays().get(0));
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_validSeatTicketAllDays_reservationCreated() throws EntityNotFoundException, ImpossibleActionException, EntityNotValidException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, true);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ReservationDTO reservationDTO = reservationService.createReservation(newReservationDTO);

        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertEquals(Integer.valueOf(1), ticketDTO.getColNum());
        assertEquals(Integer.valueOf(2), ticketDTO.getRowNum());
        assertEquals("Side", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(31), ticketDTO.getPrice());
        assertEquals(2, ticketDTO.getEventDays().size());
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1,0,0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20,0,0)));
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_validParterreTicketSingleDay_reservationCreated() throws EntityNotFoundException, ImpossibleActionException, EntityNotValidException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(26L, null, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ReservationDTO reservationDTO = reservationService.createReservation(newReservationDTO);

        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertNull(ticketDTO.getColNum());
        assertNull(ticketDTO.getRowNum());
        assertEquals("Front", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(8), ticketDTO.getPrice());
        assertEquals(1, ticketDTO.getEventDays().size());
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 20,0,0), ticketDTO.getEventDays().get(0));
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_validParterreTicketAllDays_reservationCreated() throws EntityNotFoundException, ImpossibleActionException, EntityNotValidException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(26L, null, true);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ReservationDTO reservationDTO = reservationService.createReservation(newReservationDTO);

        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertNull(ticketDTO.getColNum());
        assertNull(ticketDTO.getRowNum());
        assertEquals("Front", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(8), ticketDTO.getPrice());
        assertEquals(2, ticketDTO.getEventDays().size());
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1,0,0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20,0,0)));
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_noSuchEvent_entityNotFoundException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(10000L, tickets);

        Exception exception = assertThrows(EntityNotFoundException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Event not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_tooManyTickets_entityNotValidException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Exception exception = assertThrows(EntityNotValidException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Too many tickets in the reservation", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_eventStarted_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Event already started", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_seatDoesNotExist_entityNotFoundException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 10L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        Exception exception = assertThrows(EntityNotFoundException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Seat not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_seatTaken_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 1L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Seat is already taken", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_seatNotFreeAllDays_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 1L, true));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Seat is not free for all days", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_parterreDoesNotExist_entityNotFoundException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(25L, null, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        Exception exception = assertThrows(EntityNotFoundException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Parterre not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_parterreFull_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(50L, null, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(25L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Parterre is already fully taken", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createReservation_parterreNotFreeAllDays_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(50L, null, true));

        NewReservationDTO newReservationDTO = new NewReservationDTO(25L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class,()->reservationService.createReservation(newReservationDTO));
        assertEquals("Parterre not free for all days", exception.getMessage());
    }

    @Test
    public void cancelReservation() {
    }

    @Test
    public void payReservationCreatePayment() {
    }

    @Test
    public void payReservationExecutePayment() {
    }

    @Test
    public void createAndPayReservationCreatePayment() {
    }

    @Test
    public void createAndPayReservationExecutePayment() {
    }
}

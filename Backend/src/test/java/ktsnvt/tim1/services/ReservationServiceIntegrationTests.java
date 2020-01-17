package ktsnvt.tim1.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.exceptions.PayPalException;
import ktsnvt.tim1.model.RegisteredUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore("javax.security.*")
@PrepareForTest({ReservationService.class, Payment.class})
@ActiveProfiles("test")
public class ReservationServiceIntegrationTests {

    @Autowired
    private ReservationService reservationService;

    private void setUpPrincipal(RegisteredUser registeredUser) {
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
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0), ticketDTO.getEventDays().get(0));
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
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0)));
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
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0), ticketDTO.getEventDays().get(0));
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
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0)));
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

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(EntityNotValidException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createReservation(newReservationDTO));
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

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createReservation(newReservationDTO));
        assertEquals("Parterre not free for all days", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void cancelReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 100L;
        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.cancelReservation(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void cancelReservation_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.cancelReservation(reservationId));
        assertEquals("Reservation is already paid, therefore cannot be cancelled", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void cancelReservation_everythingValid_reservationCanceled() throws ImpossibleActionException, EntityNotFoundException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 20L;
        ReservationDTO reservationDTO = reservationService.cancelReservation(reservationId);
        assertFalse(reservationDTO.getTickets().isEmpty());
        assertNull(reservationDTO.getOrderId());
    }


    @Transactional
    @Rollback
    @Test
    public void payReservationCreatePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 100L;
        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.payReservationCreatePayment(reservationId));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void payReservationCreatePayment_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.payReservationCreatePayment(reservationId));
        assertEquals("Reservation is already paid, therefore cannot be payed again", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void payReservationCreatePayment_everythingValid_paymentCreated() throws PayPalRESTException, PayPalException, ImpossibleActionException, EntityNotFoundException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 20L;
        String paymentId = "paymentId";
        Payment payment = new Payment();
        payment.setId(paymentId);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(payment);
        PaymentDTO paymentDTO = reservationService.payReservationCreatePayment(reservationId);
        assertEquals(paymentId, paymentDTO.getPaymentID());
    }


    @Transactional
    @Rollback
    @Test
    public void payReservationExecutePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO("paymentId");
        paymentDTO.setPayerID(payerId);

        Long reservationId = 100L;
        Exception exception = assertThrows(EntityNotFoundException.class, () -> reservationService.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void payReservationExecutePayment_reservationAlreadyPaid_impossibleActionException() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO("paymentId");
        paymentDTO.setPayerID(payerId);

        Long reservationId = 1L;
        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Reservation is already paid, therefore cannot be payed again", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void payReservationExecutePayment_paymentNotForReservation_impossibleActionException() throws PayPalRESTException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Long reservationId = 20L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        ItemList itemList1 = new ItemList();
        itemList1.setShippingAddress(new ShippingAddress());
        Transaction transaction1 = new Transaction();
        transaction1.setItemList(itemList1);
        transaction1.setPayee(new Payee());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        payment.setTransactions(transactions);

        Payment paymentToBeCreated = new Payment();
        paymentToBeCreated.setId(paymentId);
        ItemList itemList2 = new ItemList();
        itemList2.setShippingAddress(new ShippingAddress());
        Transaction transaction2 = new Transaction();
        transaction2.setItemList(itemList2);
        transaction2.setPayee(new Payee());
        transaction2.setDescription("description");
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction2);
        paymentToBeCreated.setTransactions(transactions2);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(paymentToBeCreated);
        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.payReservationExecutePayment(reservationId, paymentDTO));
        assertEquals("Sent payment ID matches the payment which does not correspond to sent reservation", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void payReservationExecutePayment_everythingValid_paymentExecuted() throws PayPalRESTException, PayPalException, ImpossibleActionException, EntityNotFoundException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Long reservationId = 20L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        ItemList itemList = new ItemList();
        itemList.setShippingAddress(new ShippingAddress());
        Transaction transaction = new Transaction();
        transaction.setItemList(itemList);
        transaction.setPayee(new Payee());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        payment.setTransactions(transactions);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "execute", APIContext.class, PaymentExecution.class)).toReturn(payment);
        ReservationDTO reservationDTO = reservationService.payReservationExecutePayment(reservationId, paymentDTO);
        assertEquals(paymentId, reservationDTO.getOrderId());
    }


    @Transactional
    @Rollback
    @Test
    public void createAndPayReservationCreatePayment_everythingValid_paymentCreated() throws PayPalRESTException, PayPalException, ImpossibleActionException, EntityNotValidException, EntityNotFoundException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        String paymentId = "paymentId";
        Payment payment = new Payment();
        payment.setId(paymentId);
        ItemList itemList = new ItemList();
        itemList.setShippingAddress(new ShippingAddress());
        Transaction transaction = new Transaction();
        transaction.setItemList(itemList);
        transaction.setPayee(new Payee());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        payment.setTransactions(transactions);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(payment);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        PaymentDTO paymentDTO = reservationService.createAndPayReservationCreatePayment(newReservationDTO);

        assertEquals(paymentId, paymentDTO.getPaymentID());
    }

    @Transactional
    @Rollback
    @Test
    public void createAndPayReservationExecutePayment_paymentNotForReservation_impossibleActionException() throws PayPalRESTException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        ItemList itemList1 = new ItemList();
        itemList1.setShippingAddress(new ShippingAddress());
        Transaction transaction1 = new Transaction();
        transaction1.setItemList(itemList1);
        transaction1.setPayee(new Payee());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        payment.setTransactions(transactions);

        Payment paymentToBeCreated = new Payment();
        paymentToBeCreated.setId(paymentId);
        ItemList itemList2 = new ItemList();
        itemList2.setShippingAddress(new ShippingAddress());
        Transaction transaction2 = new Transaction();
        transaction2.setItemList(itemList2);
        transaction2.setPayee(new Payee());
        transaction2.setDescription("description");
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction2);
        paymentToBeCreated.setTransactions(transactions2);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(paymentToBeCreated);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        Exception exception = assertThrows(ImpossibleActionException.class, () -> reservationService.createAndPayReservationExecutePayment(newReservationDTO, paymentDTO));
        assertEquals("Sent payment ID matches the payment which does not correspond to sent reservation", exception.getMessage());
    }

    @Transactional
    @Rollback
    @Test
    public void createAndPayReservationExecutePayment_everythingValid_paymentExecutedAndReservationCreated() throws PayPalRESTException, PayPalException, ImpossibleActionException, EntityNotValidException, EntityNotFoundException {
        Long registeredUserId = 25L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);
        String paymentId = "paymentId";
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO(paymentId);
        paymentDTO.setPayerID(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        ItemList itemList = new ItemList();
        itemList.setShippingAddress(new ShippingAddress());
        Transaction transaction = new Transaction();
        transaction.setItemList(itemList);
        transaction.setPayee(new Payee());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        payment.setTransactions(transactions);

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        PowerMockito.mockStatic(Payment.class);
        PowerMockito.when(Payment.get((APIContext) Mockito.any(), Mockito.any())).thenReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "create", APIContext.class)).toReturn(payment);
        PowerMockito.stub(PowerMockito.method(Payment.class, "execute", APIContext.class, PaymentExecution.class)).toReturn(payment);
        ReservationDTO reservationDTO = reservationService.createAndPayReservationExecutePayment(newReservationDTO, paymentDTO);
        assertEquals(paymentId, reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertEquals(Integer.valueOf(1), ticketDTO.getColNum());
        assertEquals(Integer.valueOf(2), ticketDTO.getRowNum());
        assertEquals("Side", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(31), ticketDTO.getPrice());
        assertEquals(1, ticketDTO.getEventDays().size());
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0), ticketDTO.getEventDays().get(0));
    }

}


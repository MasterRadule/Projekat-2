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
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
public class ReservationService {

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.clientSecret}")
    private String clientSecret;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservableSeatGroupRepository reservableSeatGroupRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<ReservationDTO> getReservations(ReservationTypeDTO type, Pageable pageable) {
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        switch (type) {
            case BOUGHT:
                return reservationRepository.findByRegisteredUserIdAndOrderIdIsNotNullAndIsCancelledFalse(registeredUser.getId(), pageable)
                        .map(reservationMapper::toDTO);
            case RESERVED:
                return reservationRepository.findByRegisteredUserIdAndOrderIdIsNullAndIsCancelledFalse(registeredUser.getId(), pageable)
                        .map(reservationMapper::toDTO);
            default:
                return reservationRepository.findByRegisteredUserIdAndIsCancelledFalse(registeredUser.getId(), pageable)
                        .map(reservationMapper::toDTO);

        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ReservationDTO getReservation(Long reservationId) throws EntityNotFoundException {
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return reservationMapper.toDTO(reservationRepository
                .findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found")));
    }

    public ReservationDTO createReservation(NewReservationDTO newReservationDTO) throws EntityNotFoundException, EntityNotValidException, ImpossibleActionException {
        return reservationMapper.toDTO(reservationRepository.save(makeReservationObject(newReservationDTO, true)));
    }

    private Reservation makeReservationObject(NewReservationDTO newReservationDTO, boolean checkReservationDeadline) throws EntityNotFoundException, EntityNotValidException, ImpossibleActionException {
        Event event = eventRepository
                .findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (newReservationDTO.getTickets().size() > event.getMaxTicketsPerReservation())
            throw new EntityNotValidException("Too many tickets in the reservation");
        LocalDateTime firstEventDay =
                event.getEventDays().stream().map(EventDay::getDate).min(LocalDateTime::compareTo)
                        .orElseThrow(() -> new ImpossibleActionException("Event does not have event days"));
        long numOfDaysToEvent = ChronoUnit.DAYS.between(LocalDateTime.now(), firstEventDay);
        if (numOfDaysToEvent < 0) throw new ImpossibleActionException("Event already started");
        if (checkReservationDeadline && numOfDaysToEvent <= event.getReservationDeadlineDays())
            throw new ImpossibleActionException("Reservation deadline date passed");

        Reservation reservation = new Reservation();
        for (NewTicketDTO t : newReservationDTO.getTickets()) {
            Ticket ticket = makeTicketObject(t, event);
            reservation.getTickets().add(ticket);
            ticket.setReservation(reservation);
        }
        reservation.setEvent(event);
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        reservation.setRegisteredUser(registeredUser);
        registeredUser.getReservations().add(reservation);
        return reservation;
    }

    private Ticket makeTicketObject(NewTicketDTO ticketDTO, Event event) throws EntityNotFoundException, ImpossibleActionException {
        Ticket ticket = new Ticket();
        if (Boolean.FALSE.equals(ticketDTO.getAllDayTicket())) {
            if (ticketDTO.getSeatId() != null) {
                reserveSeatSingleDay(ticket, event, ticketDTO);
            } else {
                reserveParterreSingleDay(ticket, event, ticketDTO);
            }
        } else {
            if (ticketDTO.getSeatId() != null) {
                reserveSeatAllDays(ticket, event, ticketDTO);
            } else {
                reserveParterreAllDays(ticket, event, ticketDTO);
            }
        }
        return ticket;
    }

    private void reserveSeatSingleDay(Ticket ticket, Event event, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        Seat seat = seatRepository.findByEventAndById(event.getId(), ticketDTO.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));
        if (seat.getTicket() != null || !seat.getReservableSeatGroup().decrementFreeSeats())
            throw new ImpossibleActionException("Seat is already taken");
        ticket.getReservableSeatGroups().add(seat.getReservableSeatGroup());
        seat.getReservableSeatGroup().getTickets().add(ticket);
        ticket.getSeats().add(seat);
        seat.setTicket(ticket);
    }

    private void reserveParterreSingleDay(Ticket ticket, Event event, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        ReservableSeatGroup reservableSeatGroup = reservableSeatGroupRepository
                .findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Parterre not found"));
        if (!reservableSeatGroup.decrementFreeSeats())
            throw new ImpossibleActionException("Parterre is already fully taken");
        ticket.getReservableSeatGroups().add(reservableSeatGroup);
        reservableSeatGroup.getTickets().add(ticket);
    }

    private void reserveSeatAllDays(Ticket ticket, Event event, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        Seat seat = seatRepository.findByEventAndById(event.getId(), ticketDTO.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));
        EventSeatGroup eventSeatGroup = seat.getReservableSeatGroup().getEventSeatGroup();
        List<Seat> seats = seatRepository
                .getSeatsByRowNumAndColNum(event.getId(), eventSeatGroup.getId(), seat.getRowNum(), seat.getColNum());
        if (seats.stream().anyMatch(s -> s.getTicket() != null || !s.getReservableSeatGroup().decrementFreeSeats()))
            throw new ImpossibleActionException("Seat is not free for all days");
        seats.forEach(s -> {
            ticket.getReservableSeatGroups().add(s.getReservableSeatGroup());
            s.getReservableSeatGroup().getTickets().add(ticket);
            ticket.getSeats().add(s);
            s.setTicket(ticket);
        });
    }

    private void reserveParterreAllDays(Ticket ticket, Event event, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        ReservableSeatGroup reservableSeatGroup = reservableSeatGroupRepository
                .findByEventAndById(event.getId(), ticketDTO.getReservableSeatGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Parterre not found"));
        EventSeatGroup eventSeatGroup = reservableSeatGroup.getEventSeatGroup();
        List<ReservableSeatGroup> reservableSeatGroups = reservableSeatGroupRepository
                .findByEventAndByEventSeatGroup(event.getId(), eventSeatGroup.getId());
        if (reservableSeatGroups.stream().anyMatch(esg -> !esg.decrementFreeSeats()))
            throw new ImpossibleActionException("Parterre not free for all days");
        reservableSeatGroups.forEach(rsg -> {
            ticket.getReservableSeatGroups().add(rsg);
            rsg.getTickets().add(ticket);
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) throws EntityNotFoundException, ImpossibleActionException {
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Reservation reservation = reservationRepository
                .findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be cancelled");
        cancelReservationRemoveConnections(reservation);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    public void cancelReservationRemoveConnections(Reservation reservation) {
        reservation.setCancelled(true);
        reservation.getTickets().forEach(ticket -> {
            for (ReservableSeatGroup rsg : ticket.getReservableSeatGroups()) {
                rsg.getTickets().remove(ticket);
                rsg.incrementFreeSeats();
            }
            ticket.getReservableSeatGroups().clear();
            for(Seat seat : ticket.getSeats()){
                seat.setTicket(null);
            }
            ticket.getSeats().clear();
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PaymentDTO payReservationCreatePayment(Long reservationId) throws EntityNotFoundException, ImpossibleActionException, PayPalRESTException, PayPalException {
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Reservation reservation = reservationRepository
                .findByIdAndIsCancelledFalseAndRegisteredUserId(reservationId, registeredUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be payed again");

        Payment createdPayment = makePaymentObject(reservation);
        return new PaymentDTO(createdPayment.getId());
    }

    public ReservationDTO payReservationExecutePayment(Long reservationId, PaymentDTO paymentDTO) throws PayPalRESTException, EntityNotFoundException, ImpossibleActionException, PayPalException {
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Reservation reservation = reservationRepository
                .findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be payed again");

        if (paymentIsNotForReservation(reservation, paymentDTO)) {
            throw new ImpossibleActionException("Sent payment ID matches the payment which does not correspond to sent reservation");
        }

        Payment executedPayment = executePayment(paymentDTO.getPaymentID(), paymentDTO.getPayerID());
        reservation.setOrderId(executedPayment.getId());
        ReservationDTO reservationDTO = reservationMapper.toDTO(reservationRepository.save(reservation));
        try {
            emailService.sendReservationBoughtEmail(reservation);
        } catch (MessagingException e) {
            System.out.println("Message not sent because of exception: " + e.getMessage());
        }
        return reservationDTO;
    }

    public PaymentDTO createAndPayReservationCreatePayment(NewReservationDTO newReservationDTO) throws EntityNotFoundException, EntityNotValidException, ImpossibleActionException, PayPalRESTException, PayPalException {
        Reservation reservation = makeReservationObject(newReservationDTO, false);
        Payment createdPayment = makePaymentObject(reservation);
        entityManager.clear(); // clear persistence context in order not to save reservation in database
        return new PaymentDTO(createdPayment.getId());
    }

    public ReservationDTO createAndPayReservationExecutePayment(NewReservationDTO newReservationDTO, PaymentDTO paymentDTO) throws EntityNotFoundException, EntityNotValidException, ImpossibleActionException, PayPalRESTException, PayPalException {
        Reservation reservation = makeReservationObject(newReservationDTO, false);

        if (paymentIsNotForReservation(reservation, paymentDTO)) {
            throw new ImpossibleActionException("Sent payment ID matches the payment which does not correspond to sent reservation");
        }

        Payment executedPayment = executePayment(paymentDTO.getPaymentID(), paymentDTO.getPayerID());
        reservation.setOrderId(executedPayment.getId());

        ReservationDTO reservationDTO = reservationMapper.toDTO(reservationRepository.save(reservation));
        try {
            emailService.sendReservationBoughtEmail(reservation);
        } catch (MessagingException e) {
            System.out.println("Message not sent because of exception: " + e.getMessage());
        }
        return reservationDTO;
    }

    private Payment makePaymentObject(Reservation reservation) throws PayPalRESTException, PayPalException {
        ItemList itemList = new ItemList();
        List<Ticket> tickets = new ArrayList<>(reservation.getTickets());
        tickets.sort((t1, t2) -> (int) (t1.getId() - t2.getId())); // sorting to avoid randomness of items' order
        itemList.setItems(new ArrayList<>());
        for (Ticket ticket : tickets) {
            Item item = new Item();
            item.setDescription("KTSNVT - Ticket");
            item.setName("TicketID: " + ticket.getId());
            item.setCurrency("EUR");
            item.setPrice(String.valueOf(ticket.getReservableSeatGroups().stream().mapToDouble(
                    rsg -> rsg.getEventSeatGroup().getPrice()).sum()));
            item.setQuantity("1");
            itemList.getItems().add(item);
        }

        Transaction transaction = new Transaction();
        transaction.setItemList(itemList);
        Amount amount = new Amount();
        amount.setCurrency("EUR");
        amount.setTotal(String.valueOf(itemList.getItems().stream().mapToDouble(item -> Double.parseDouble(item.getPrice())).sum()));
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl("/");
        redirectUrls.setCancelUrl("/");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment createdPayment = payment.create(context);
        if (createdPayment == null) {
            throw new PayPalException("Payment not created");
        }
        return createdPayment;
    }

    private boolean paymentIsNotForReservation(Reservation reservation, PaymentDTO paymentDTO) throws PayPalRESTException, PayPalException {
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment payment = Payment.get(context, paymentDTO.getPaymentID());
        if (payment == null) {
            throw new PayPalException("Payment with sent payment ID does not exist");
        }
        Payment paymentMade = makePaymentObject(reservation);
        paymentMade.getTransactions().get(0).setPayee(payment.getTransactions().get(0).getPayee());
        paymentMade.getTransactions().get(0).getItemList().setShippingAddress(payment.getTransactions().get(0).getItemList().getShippingAddress());
        return !payment.getTransactions().equals(paymentMade.getTransactions());
    }

    private Payment executePayment(String paymentID, String payerID) throws PayPalRESTException, PayPalException {
        Payment payment = new Payment();
        payment.setId(paymentID);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerID);
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment executedPayment = payment.execute(context, paymentExecution);
        if (executedPayment == null) {
            throw new PayPalException("Payment not executed");
        }
        return executedPayment;
    }
}

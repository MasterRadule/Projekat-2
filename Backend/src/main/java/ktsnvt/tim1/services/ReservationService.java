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
import ktsnvt.tim1.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
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
    private EventDayRepository eventDayRepository;

    @Autowired
    private EventSeatGroupRepository eventSeatGroupRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservableSeatGroupRepository reservableSeatGroupRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    public Page<ReservationDTO> getReservations(ReservationTypeDTO type, Pageable pageable) {
        switch (type) {
            case BOUGHT:
                return reservationRepository.findByOrderIdIsNotNullAndIsCancelledFalse(pageable).map(reservationMapper::toDTO);
            case RESERVED:
                return reservationRepository.findByOrderIdIsNullAndIsCancelledFalse(pageable).map(reservationMapper::toDTO);
            default:
                return reservationRepository.findAll(pageable).map(reservationMapper::toDTO);

        }
    }

    public ReservationDTO getReservation(Long id) throws EntityNotFoundException {
        return reservationMapper.toDTO(reservationRepository.findByIdAndIsCancelledFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found")));
    }

    public ReservationDTO createReservation(NewReservationDTO newReservationDTO) throws EntityNotFoundException, EntityNotValidException, ImpossibleActionException {
        Event event = eventRepository.findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(newReservationDTO.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (newReservationDTO.getTickets().size() > event.getMaxTicketsPerReservation())
            throw new EntityNotValidException("Too many tickets in the reservation");
        Date firstEventDay = event.getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
        int numOfDaysToEvent = (int) TimeUnit.DAYS.convert(Math.abs(firstEventDay.getTime() - new Date().getTime()), TimeUnit.MILLISECONDS);
        if (numOfDaysToEvent < 0) throw new ImpossibleActionException("Event already started");
        if (numOfDaysToEvent <= event.getReservationDeadlineDays())
            throw new ImpossibleActionException("Reservation deadline date passed");

        Reservation reservation = new Reservation();
        for (NewTicketDTO t : newReservationDTO.getTickets()) {
            makeTicket(t, reservation);
        }
        reservation.setEvent(event);
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservation.setRegisteredUser(registeredUser);
        registeredUser.getReservations().add(reservation);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    private void makeTicket(NewTicketDTO ticketDTO, Reservation reservation) throws EntityNotFoundException, ImpossibleActionException {
        Ticket ticket = new Ticket();
        if (!ticketDTO.getAllDayTicket()) {
            if (ticketDTO.getSeatId() != null) {
                reserveSeatSingleDay(ticket, ticketDTO);
            } else {
                reserveParterreSingleDay(ticket, ticketDTO);
            }
        } else {
            if (ticketDTO.getSeatId() != null) {
                reserveSeatAllDays(ticket, ticketDTO);
            } else {
                reserveParterreAllDays(ticket, ticketDTO);
            }
        }
        reservation.getTickets().add(ticket);
        ticket.setReservation(reservation);
    }

    private void reserveSeatSingleDay(Ticket ticket, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        Seat seat = seatRepository.findById(ticketDTO.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));
        if (seat.getTicket() != null || !seat.getReservableSeatGroup().decrementFreeSeats())
            throw new ImpossibleActionException("Seat is already taken");
        ticket.getReservableSeatGroups().add(seat.getReservableSeatGroup());
        seat.getReservableSeatGroup().getTickets().add(ticket);
        ticket.getSeats().add(seat);
        seat.setTicket(ticket);
    }

    private void reserveParterreSingleDay(Ticket ticket, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        ReservableSeatGroup reservableSeatGroup = reservableSeatGroupRepository.findById(ticketDTO.getReservableSeatGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Parterre not found"));
        if (!reservableSeatGroup.decrementFreeSeats())
            throw new ImpossibleActionException("Parterre is already fully taken");
        ticket.getReservableSeatGroups().add(reservableSeatGroup);
        reservableSeatGroup.getTickets().add(ticket);
    }

    private void reserveSeatAllDays(Ticket ticket, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        Seat seat = seatRepository.findById(ticketDTO.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));
        EventSeatGroup eventSeatGroup = seat.getReservableSeatGroup().getEventSeatGroup();
        List<Seat> seats = seatRepository.getSeatsByRowNumAndColNum(eventSeatGroup.getId(), seat.getRowNum(), seat.getColNum());
        if (seats.stream().anyMatch((s) -> s.getTicket() == null || !s.getReservableSeatGroup().decrementFreeSeats()))
            throw new ImpossibleActionException("Seat is not free for all days");
        seats.forEach((s) -> {
            ticket.getReservableSeatGroups().add(s.getReservableSeatGroup());
            s.getReservableSeatGroup().getTickets().add(ticket);
            ticket.getSeats().add(s);
            s.setTicket(ticket);
        });
    }

    private void reserveParterreAllDays(Ticket ticket, NewTicketDTO ticketDTO) throws EntityNotFoundException, ImpossibleActionException {
        ReservableSeatGroup reservableSeatGroup = reservableSeatGroupRepository.findById(ticketDTO.getReservableSeatGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Parterre not found"));
        EventSeatGroup eventSeatGroup = reservableSeatGroup.getEventSeatGroup();
        List<ReservableSeatGroup> reservableSeatGroups = reservableSeatGroupRepository.findByEventSeatGroup(eventSeatGroup.getId());
        if (reservableSeatGroups.stream().anyMatch((esg) -> !esg.decrementFreeSeats()))
            throw new ImpossibleActionException("Parterre not free for all days");
        reservableSeatGroups.forEach((rsg) -> {
            ticket.getReservableSeatGroups().add(rsg);
            rsg.getTickets().add(ticket);
        });
    }

    public ReservationDTO cancelReservation(Long id) throws EntityNotFoundException, ImpossibleActionException {
        Reservation reservation = reservationRepository.findByIdAndIsCancelledFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be cancelled");
        reservation.setCancelled(true);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    public Object payReservationCreatePayment(Long reservationId) throws EntityNotFoundException, ImpossibleActionException, PayPalRESTException, PayPalException {
        Reservation reservation = reservationRepository.findByIdAndIsCancelledFalse(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be payed again");

        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment createdPayment = createPaymentObject(reservationId, reservation.getTickets().stream().mapToDouble(
                t -> t.getReservableSeatGroups().stream().mapToDouble(
                        rsg -> rsg.getEventSeatGroup().getPrice()).sum()).sum())
                .create(context);
        if (createdPayment == null) {
            throw new PayPalException("Payment not created");
        }
        return new PaymentDTO(createdPayment.getId());
    }

    private Payment createPaymentObject(long reservationId, double price) {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(price));

        Item item = new Item();
        item.setDescription("KTSNVT - Reservation");
        item.setName(String.valueOf(reservationId));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.getItemList().getItems().add(item);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        return payment;
    }

    public Object payReservationExecutePayment(Long reservationId, PaymentDTO paymentDTO) throws PayPalRESTException, EntityNotFoundException, ImpossibleActionException, PayPalException {
        Reservation reservation = reservationRepository.findByIdAndIsCancelledFalse(reservationId) //TODO optimistic lock
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be payed again");

        Payment payment = new Payment();
        payment.setId(paymentDTO.getPaymentID());

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(paymentDTO.getPayerID());
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment createdPayment = payment.execute(context, paymentExecution);
        if (createdPayment == null) {
            throw new PayPalException("Payment not executed");
        }

        reservation.setOrderId(createdPayment.getId());
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }
}
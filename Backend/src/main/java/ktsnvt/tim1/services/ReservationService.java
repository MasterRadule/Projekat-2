package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.NewReservationDTO;
import ktsnvt.tim1.DTOs.NewTicketDTO;
import ktsnvt.tim1.DTOs.ReservationDTO;
import ktsnvt.tim1.DTOs.ReservationTypeDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.mappers.ReservationMapper;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {
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
        if (numOfDaysToEvent < 0) throw new EntityNotValidException("Event already started");
        if (numOfDaysToEvent <= event.getReservationDeadlineDays())
            throw new EntityNotValidException("Reservation deadline date passed");

        HashSet<Ticket> tickets = new HashSet<Ticket>();
        for (NewTicketDTO t : newReservationDTO.getTickets()) {
            makeTicket(t, tickets);
        }
        Reservation reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setTickets(tickets);
        RegisteredUser registeredUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservation.setRegisteredUser(registeredUser);
        registeredUser.getReservations().add(reservation);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    private void makeTicket(NewTicketDTO ticketDTO, Set<Ticket> tickets) throws EntityNotFoundException, ImpossibleActionException {
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
        tickets.add(ticket);
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

    public Object cancelReservation(Long id) throws EntityNotFoundException, ImpossibleActionException {
        Reservation reservation = reservationRepository.findByIdAndIsCancelledFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (reservation.getOrderId() != null)
            throw new ImpossibleActionException("Reservation is already paid, therefore cannot be cancelled");
        reservation.setCancelled(true);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }
}
package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.DTOs.EventDTO;
import ktsnvt.tim1.DTOs.EventDayDTO;
import ktsnvt.tim1.DTOs.SearchEventsDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    public Page<EventDTO> getEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventDTO::new);
    }

    public EventDTO getEvent(Long id) throws EntityNotFoundException {
        return new EventDTO(eventRepository.findByIdAndIsCancelledFalse(id).orElseThrow(() -> new EntityNotFoundException("Event not found")));
    }

    public EventDTO createEvent(EventDTO event) throws EntityNotValidException {
        return new EventDTO(eventRepository.save(event.convertToEntity()));
    }

    public EventDTO editEvent(EventDTO event) throws EntityNotFoundException, EntityAlreadyExistsException, EntityNotValidException {
        if (event.getId() == null)
            throw new EntityNotValidException("Event must have an ID");

        Event e = eventRepository.findByIdAndIsCancelledFalse(event.getId()).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!e.getName().equalsIgnoreCase(event.getName()) && eventRepository.findOneByName(event.getName()) != null) {
            throw new EntityAlreadyExistsException("Event with given name already exists");
        }
        e.setName(event.getName());
        e.setDescription(event.getDescription());
        e.setCategory(EventCategory.valueOf(event.getCategory()));
        e.setActiveForReservations(event.isActiveForReservations());
        e.setMaxReservationsPerUser(event.getMaxReservationsPerUser());
        e.setCancelled(event.isCancelled());

        addAndRemoveEventDays(e, event);
        checkNumberOfReservationDeadlineDays(e, event);

        return new EventDTO(eventRepository.save(e));
    }

    public EventDTO setEventLocationAndSeatGroups(LocationSeatGroupDTO seatGroupsDTO) throws EntityNotFoundException, EntityNotValidException {
        Event e = eventRepository.findByIdAndIsCancelledFalse(seatGroupsDTO.getEventID()).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Location l = locationRepository.findById(seatGroupsDTO.getLocationID()).orElseThrow(() -> new EntityNotFoundException("Location not found"));

        changeLocation(e, l);
        disableEventGroups(e, seatGroupsDTO);
        enableEventGroups(e, l, seatGroupsDTO);

        return new EventDTO(eventRepository.save(e));
    }

    private void addAndRemoveEventDays(Event e, EventDTO event) throws EntityNotValidException {
        Set<EventDay> daysFromDTO = new HashSet<>();
        ArrayList<EventDayDTO> evDays = event.getEventDays();
        for (EventDayDTO eDay : evDays) {
            daysFromDTO.add(eDay.convertToEntity());
        }

        Set<EventDay> eventDays = new HashSet<>(e.getEventDays());
        eventDays.removeAll(daysFromDTO);


        boolean invalidRemove = e.getEventDays().stream().anyMatch(eDay -> {
            if (eventDays.contains(eDay)) {
                Set<ReservableSeatGroup> resSeatGroups = eDay.getReservableSeatGroups();
                for (ReservableSeatGroup rsg : resSeatGroups) {
                    if (!rsg.getTickets().isEmpty())
                        return true;
                }
            }
            return false;
        });
        if (invalidRemove) {
            throw new EntityNotValidException("Event day for which reservations exist cannot be removed");
        }
        else {
            e.getEventDays().removeIf(eDay -> {
                if (eventDays.contains(eDay)) {
                    Set<ReservableSeatGroup> resSeatGroups = eDay.getReservableSeatGroups();
                    for (ReservableSeatGroup rsg : resSeatGroups) {
                        if (rsg.getTickets().isEmpty())
                            return true;
                    }
                }
                return false;
            });
        }

        daysFromDTO.removeAll(e.getEventDays());
        daysFromDTO.forEach(eDay -> {
            eDay.setEvent(e);
            e.getEventDays().add(eDay);
        });
    }

    private void checkNumberOfReservationDeadlineDays(Event e, EventDTO event) throws EntityNotValidException {
        Date firstEventDay = e.getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
        int numOfDaysToEvent = (int) TimeUnit.DAYS.convert(Math.abs(firstEventDay.getTime() - new Date().getTime()), TimeUnit.MILLISECONDS);
        if (event.getReservationDeadlineDays() > numOfDaysToEvent)
            throw new EntityNotValidException("Number of reservation deadline days must " +
                    "be less than number of days left until the event");
        e.setReservationDeadlineDays(event.getReservationDeadlineDays());
    }

    private void changeLocation(Event e, Location l) throws EntityNotValidException{
        if (!e.getLocation().equals(l)) {
            Set<EventSeatGroup> eventSeatGroups = e.getEventSeatGroups();
            for (EventSeatGroup esg : eventSeatGroups) {
                Set<ReservableSeatGroup> resSeatGroups = esg.getReservableSeatGroups();
                for (ReservableSeatGroup rsg : resSeatGroups) {
                    if (!rsg.getTickets().isEmpty())
                        throw new EntityNotValidException("Location cannot be changed if reservation for event exist");
                }
            }
        }
    }

    private void disableEventGroups(Event e, LocationSeatGroupDTO seatGroupsDTO) {
        e.getEventSeatGroups().removeIf(esg -> seatGroupsDTO.getEventSeatGroups().stream()
                .anyMatch(sgDTO -> {
                    if (sgDTO.getSeatGroupID().longValue() == esg.getSeatGroup().getId().longValue()) {
                        Set<ReservableSeatGroup> resSeatGroups = esg.getReservableSeatGroups();
                        for (ReservableSeatGroup rsg : resSeatGroups) {
                            if (rsg.getTickets().isEmpty())
                                return true;
                        }
                    }
                    return false;
                }));
    }

    private void enableEventGroups(Event e, Location l, LocationSeatGroupDTO seatGroupsDTO) {
        ArrayList<EventSeatGroupDTO> seatGroups = seatGroupsDTO.getEventSeatGroups();
        SeatGroup seatGroup;
        EventSeatGroup eventSeatGroup;
        for (EventSeatGroupDTO esgDTO : seatGroups) {
            seatGroup = l.getSeatGroups().stream()
                    .filter(seatG -> seatG.getId().longValue() == esgDTO.getSeatGroupID().longValue()).findFirst().orElse(null);

            if (seatGroup != null) {
                eventSeatGroup = e.getEventSeatGroups().stream()
                        .filter(seatG -> seatG.getSeatGroup().getId().longValue() == esgDTO.getSeatGroupID().longValue()).findFirst().orElse(null);
                if (eventSeatGroup != null) {
                    eventSeatGroup.setPrice(esgDTO.getPrice());
                    continue;
                }

                EventSeatGroup esg = new EventSeatGroup();
                esg.setPrice(esgDTO.getPrice());
                esg.setSeatGroup(seatGroup);
                e.getEventDays().forEach(eventDay -> new ReservableSeatGroup(eventDay, esg));

                e.getEventSeatGroups().add(esg);
            }
        }
    }

    public Page<EventDTO> searchEvents(SearchEventsDTO searchDTO, Pageable pageable) throws EntityNotValidException {
        String name = searchDTO.getName().toLowerCase() + "%";
        String category = searchDTO.getCategory().equals("") ? "%" : searchDTO.getCategory();
        Page<Event> events = eventRepository.searchEvents(name, category, searchDTO.getLocationID(), pageable);

        if (!searchDTO.getFromDate().equals("") && !searchDTO.getToDate().equals("")) {
            ArrayList<EventDTO> eventsDTO = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date fromDate;
            Date toDate;
            try {
                fromDate = formatter.parse(searchDTO.getFromDate());
                toDate = formatter.parse(searchDTO.getToDate());
            } catch (ParseException e) {
                throw new EntityNotValidException("Dates are in invalid format");
            }

            events.stream().forEach(e -> {
                for (EventDay ev : e.getEventDays()) {
                    if (fromDate.compareTo(ev.getDate()) * ev.getDate().compareTo(toDate) >= 0) {
                        eventsDTO.add(new EventDTO(e));
                        return;
                    }
                }
            });

            return new PageImpl<>(eventsDTO);
        }
        else {
            return new PageImpl<>(events.stream().map(EventDTO::new).collect(Collectors.toList()));
        }

    }
}

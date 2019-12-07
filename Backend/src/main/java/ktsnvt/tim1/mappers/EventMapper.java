package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.EventDTO;
import ktsnvt.tim1.DTOs.EventDayDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.model.EventDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventMapper implements IMapper<Event, EventDTO> {

    @Autowired
    private EventDayMapper eventDayMapper;

    @Override
    public Event toEntity(EventDTO eventDTO) throws EntityNotValidException {
        Event e = new Event();
        e.setId(null);
        e.setName(eventDTO.getName());
        e.setDescription(eventDTO.getDescription());
        e.setCategory(EventCategory.valueOf(eventDTO.getCategory()));
        e.setActiveForReservations(eventDTO.isActiveForReservations());
        e.setMaxTicketsPerReservation(eventDTO.getMaxTicketsPerReservation());
        e.setReservationDeadlineDays(eventDTO.getReservationDeadlineDays());
        e.setCancelled(eventDTO.isCancelled());
        Set<EventDay> eventDays = new HashSet<>();
        for (EventDayDTO d : eventDTO.getEventDays()) {
            EventDay eventDay = eventDayMapper.toEntity(d);
            eventDay.setEvent(e);
            eventDays.add(eventDay);
        }
        e.setEventDays(eventDays);
        return e;
    }

    @Override
    public EventDTO toDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setCategory(event.getCategory().name());
        eventDTO.setActiveForReservations(event.getActiveForReservations());
        eventDTO.setReservationDeadlineDays(event.getReservationDeadlineDays());
        eventDTO.setCancelled(event.getCancelled());
        eventDTO.setMaxTicketsPerReservation(event.getMaxTicketsPerReservation());
        eventDTO.setEventDays(event.getEventDays().stream().map(ev -> eventDayMapper.toDTO(ev))
                .collect(Collectors.toCollection(ArrayList::new)));

        return eventDTO;
    }
}

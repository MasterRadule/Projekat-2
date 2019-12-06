package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.EventDayDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.EventDay;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class EventDayMapper implements IMapper<EventDay, EventDayDTO> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    @Override
    public EventDay toEntity(EventDayDTO eventDayDTO) throws EntityNotValidException {
        EventDay eventDay = new EventDay();
        eventDay.setId(null);
        try {
            eventDay.setDate(LocalDateTime.parse(eventDayDTO.getDate(), formatter));
        } catch (DateTimeParseException e) {
            throw new EntityNotValidException("Dates of event days are in invalid format");
        }
        return eventDay;
    }

    @Override
    public EventDayDTO toDTO(EventDay eventDay) {
        EventDayDTO eventDayDTO = new EventDayDTO();
        eventDayDTO.setId(eventDay.getId());
        eventDayDTO.setDate(formatter.format(eventDay.getDate()));

        return eventDayDTO;
    }
}

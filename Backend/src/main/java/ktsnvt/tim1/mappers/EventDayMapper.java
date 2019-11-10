package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.EventDayDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.EventDay;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class EventDayMapper implements IMapper<EventDay, EventDayDTO> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy. HH:mm");

    @Override
    public EventDay toEntity(EventDayDTO eventDayDTO) throws EntityNotValidException {
        EventDay eventDay = new EventDay();
        eventDay.setId(null);
        try {
            eventDay.setDate(formatter.parse(eventDayDTO.getDate()));
        } catch (ParseException e) {
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

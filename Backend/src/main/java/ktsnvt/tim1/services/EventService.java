package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.EventDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Page<EventDTO> getEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventDTO::new);
    }

    public EventDTO getEvent(Long id) throws EntityNotFoundException {
        return new EventDTO(eventRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public EventDTO createEvent(EventDTO event) throws ParseException {
        return new EventDTO(eventRepository.save(event.convertToEntity()));
    }
}

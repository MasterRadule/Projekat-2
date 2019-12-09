package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EventRepositoryIntegrationTests {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void searchEvents_searchParametersProvided_pageReturned() {
        String name = "cl%";
        EventCategory category = EventCategory.Music;
        Long locationID = 25L;
        Pageable pageable = PageRequest.of(0, 5);

        Page<Event> events = eventRepository.searchEvents(name, category, locationID, pageable);

        assertEquals(1, events.getNumberOfElements());
    }

    @Test
    void searchEvents_searchParametersEmpty_pageReturned() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<Event> events = eventRepository.searchEvents("%", null, null, pageable);

        assertEquals(eventRepository.count(), events.getTotalElements());
    }

    @Test
    void searchEvents_categoryAndLocationIdAreNull_pageReturned() {
        String name = "co%";
        Pageable pageable = PageRequest.of(0, 5);

        Page<Event> events = eventRepository.searchEvents(name, null, null, pageable);

        assertEquals(4, events.getNumberOfElements());
    }

    @Test
    void searchEvents_categoryIsNotNull_pageReturned() {
        Pageable pageable = PageRequest.of(0, 5);
        EventCategory category = EventCategory.Sport;

        Page<Event> events = eventRepository.searchEvents("%", category, null, pageable);

        assertEquals(7, events.getTotalElements());
    }

    @Test
    void searchEvents_locationIdIsNotNull_pageReturned() {
        Pageable pageable = PageRequest.of(0, 5);
        Long locationID = 5L;

        Page<Event> events = eventRepository.searchEvents("%", null, locationID, pageable);

        assertEquals(1, events.getNumberOfElements());
    }
}

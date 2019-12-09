package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.EventDTO;
import ktsnvt.tim1.DTOs.EventDayDTO;
import ktsnvt.tim1.DTOs.EventSeatGroupDTO;
import ktsnvt.tim1.DTOs.LocationSeatGroupDTO;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.MediaFileRepository;
import ktsnvt.tim1.utils.RestResponsePage;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    void getEvents_eventsReturned() {
        ParameterizedTypeReference<RestResponsePage<EventDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<EventDTO>>() {
        };

        ResponseEntity<RestResponsePage<EventDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/events?page=0&size=5"), HttpMethod.GET, null, responseType);

        List<EventDTO> events = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(5, events.size());
    }

    @Test
    void getEvent_eventExists_eventReturned() {
        ResponseEntity<EventDTO> result = testRestTemplate.exchange(createURLWithPort("/events/1"), HttpMethod.GET,
                        null, EventDTO.class);

        EventDTO event = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(event);
        assertEquals(1L, event.getId().longValue());
    }

    @Test
    void getEvent_eventDoesNotExist_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/31"), HttpMethod.GET,
                null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_eventCreated() {
        EventDTO newDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        EventDayDTO eventDay = new EventDayDTO(null, "10.12.2019. 16:30");
        newDTO.getEventDays().add(eventDay);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);

        long initialSize = eventRepository.count();

        HttpEntity<EventDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<EventDTO> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.POST, entity, EventDTO.class);

        EventDTO event = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(event);
        assertEquals(newDTO.getName(), event.getName());
        assertEquals(newDTO.getDescription(), event.getDescription());
        assertEquals(newDTO.getCategory(), event.getCategory());
        assertEquals(newDTO.isCancelled(), event.isCancelled());
        assertTrue(event.isActiveForReservations());
        assertEquals(1, (int) event.getReservationDeadlineDays());
        assertEquals(3, (int) event.getMaxTicketsPerReservation());
        assertEquals(1, event.getEventDays().size());
        assertNotEquals(null, event.getId());

        Page<Event> eventPage = eventRepository.findAll(PageRequest.of(0, 5));
        assertEquals(initialSize + 1, eventPage.getTotalElements());
    }

    @Test
    void createEvent_eventDayIsInvalid_errorMessageReturned() {
        EventDTO newDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        EventDayDTO eventDay = new EventDayDTO(null, "10.2019. 16:30");
        newDTO.getEventDays().add(eventDay);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);

        HttpEntity<EventDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.POST, entity, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Dates of event days are in invalid format", errorMessage);
    }

    @Transactional
    @Rollback
    @Test
    void uploadEventsPicturesAndVideos_picturesAndVideosUploaded() throws Exception {
        Long eventID = 1L;
        Random r = new Random();

        byte[] image = new byte[20];
        r.nextBytes(image);

        byte[] video = new byte[20];
        r.nextBytes(video);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        File file1 = new File("img.png");
        FileUtils.writeByteArrayToFile(file1, image);
        parameters.add("files", new FileSystemResource(file1));

        File file2 = new File("video.mp4");
        FileUtils.writeByteArrayToFile(file2, video);
        parameters.add("files", new FileSystemResource(file2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/1/pictures-and-videos"),
              HttpMethod.POST, requestEntity, String.class);

        file1.delete();
        file2.delete();

        Optional<Event> eventOptional = eventRepository.findById(eventID);
        Event e = null;
        if (eventOptional.isPresent())
            e = eventOptional.get();

        assertNotNull(e);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Files uploaded successfully", result.getBody());
        assertEquals(3, eventRepository.getOne(eventID).getPicturesAndVideos().size());
    }

    @Test
    void uploadEventsPicturesAndVideos_eventDoesNotExist_errorMessageReturned() throws Exception {
        Random r = new Random();

        byte[] image = new byte[20];
        r.nextBytes(image);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        File file1 = new File("img.png");
        FileUtils.writeByteArrayToFile(file1, image);
        parameters.add("files", new FileSystemResource(file1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/31/pictures-and-videos"),
                HttpMethod.POST, requestEntity, String.class);

        file1.delete();

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Test
    void uploadEventsPicturesAndVideos_invalidFileType_errorMessageReturned() throws Exception {
        Random r = new Random();

        byte[] txt = new byte[20];
        r.nextBytes(txt);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        File file1 = new File("img.txt");
        FileUtils.writeByteArrayToFile(file1, txt);
        parameters.add("files", new FileSystemResource(file1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/1/pictures-and-videos"),
                HttpMethod.POST, requestEntity, String.class);

        file1.delete();

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid file type", errorMessage);
    }

    @Test
    void getEventsPicturesAndVideos_eventDoesNotExist_errorMessageReturned() {
       ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/31/pictures-and-videos"),
                HttpMethod.GET, null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Test
    void getEventsPicturesAndVideos_eventExists_picturesAndVideosReturned() {
        ResponseEntity<Set> result = testRestTemplate.exchange(createURLWithPort("/events/1/pictures-and-videos"),
                HttpMethod.GET, null, Set.class);

        Set files = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(files);
        assertEquals(1, files.size());
    }

    @Transactional
    @Rollback
    @Test
    void deleteMediaFile_eventExistsAndMediaFileExists_mediaFileDeleted() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/1/pictures-and-videos/1"),
                HttpMethod.DELETE, null, String.class);

        String message = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("File deleted successfully", message);
        assertEquals(1, eventRepository.getOne(1L).getPicturesAndVideos().size());
    }

    @Test
    void deleteMediaFile_eventDoesNotExist_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/31/pictures-and-videos/51"),
                HttpMethod.DELETE, null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Test
    void deleteMediaFile_eventExistsAndMediaFileDoesNotExist_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/1/pictures-and-videos/51"),
                HttpMethod.DELETE, null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("File not found", errorMessage);
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventEdited() {
        Long eventID = 1L;
        EventDTO newDTO = new EventDTO(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);
        EventDayDTO eventDayDTO1 = new EventDayDTO(1L, "01.01.2020. 00:00");
        EventDayDTO eventDayDTO2 = new EventDayDTO(26L, "20.02.2020. 00:00");
        newDTO.getEventDays().add(eventDayDTO1);
        newDTO.getEventDays().add(eventDayDTO2);

        ResponseEntity<EventDTO> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.PUT, new HttpEntity<>(newDTO), EventDTO.class);

        EventDTO event = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(event);
        assertEquals(newDTO.getName(), event.getName());
        assertEquals(newDTO.getDescription(), event.getDescription());
        assertEquals(newDTO.getCategory(), event.getCategory());
        assertEquals(newDTO.isCancelled(), event.isCancelled());
        assertTrue(event.isActiveForReservations());
        assertEquals(1, (int) event.getReservationDeadlineDays());
        assertEquals(3, (int) event.getMaxTicketsPerReservation());
        assertEquals(2, event.getEventDays().size());
        assertEquals(eventID, event.getId());
    }

    @Test
    void editEvent_eventDoesNotExist_errorMessageReturned() {
        Long eventID = 31L;
        EventDTO newDTO = new EventDTO(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);
        EventDayDTO eventDayDTO1 = new EventDayDTO(1L, "01.01.2020. 00:00");
        EventDayDTO eventDayDTO2 = new EventDayDTO(26L, "20.02.2020. 00:00");
        newDTO.getEventDays().add(eventDayDTO1);
        newDTO.getEventDays().add(eventDayDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.PUT, new HttpEntity<>(newDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Test
    void editEvent_eventIdIsNull_errorMessageReturned() {
        EventDTO newDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);
        EventDayDTO eventDayDTO1 = new EventDayDTO(1L, "01.01.2020. 00:00");
        EventDayDTO eventDayDTO2 = new EventDayDTO(26L, "20.02.2020. 00:00");
        newDTO.getEventDays().add(eventDayDTO1);
        newDTO.getEventDays().add(eventDayDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.PUT, new HttpEntity<>(newDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Event must have an ID", errorMessage);
    }

    @Test
    void editEvent_eventNameIsTaken_errorMessageReturned() {
        Long eventID = 1L;
        EventDTO newDTO = new EventDTO(eventID, "Tabtectar", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setActiveForReservations(true);
        newDTO.setReservationDeadlineDays(1);
        newDTO.setMaxTicketsPerReservation(3);
        EventDayDTO eventDayDTO1 = new EventDayDTO(1L, "01.01.2020. 00:00");
        EventDayDTO eventDayDTO2 = new EventDayDTO(26L, "20.02.2020. 00:00");
        newDTO.getEventDays().add(eventDayDTO1);
        newDTO.getEventDays().add(eventDayDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events"),
                HttpMethod.PUT, new HttpEntity<>(newDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Event with given name already exists", errorMessage);
    }

    @Test
    void searchEvents_pageReturned() {
        ParameterizedTypeReference<RestResponsePage<EventDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<EventDTO>>() {
        };

        ResponseEntity<RestResponsePage<EventDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/events/search?name=&category=&locationID=&fromDate=07.01.2020. 12:30&toDate=13.01.2020. 13:30&page=0&size=5"),
                HttpMethod.GET, null, responseType);

        List<EventDTO> events = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, events.size());
    }

    @Test
    void searchEvents_searchDatesAreInvalid_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/events/search?name=&category=&locationID=&fromDate=07.2020. 12:30&toDate=13.2020. 13:30&page=0&size=5"),
                HttpMethod.GET, null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Dates are in invalid format", errorMessage);
    }

    @Transactional
    @Rollback
    @Test
    void setEventLocationAndSeatGroups_locationAndSeatGroupsSet() {
        Long eventID = 1L;
        Long locationID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(1L);
        esgDTO1.setPrice(200.0);
        esgDTO1.setFreeSeats(200);
        EventSeatGroupDTO esgDTO2 = new EventSeatGroupDTO(26L);
        esgDTO2.setPrice(300.0);
        esgDTO2.setFreeSeats(300);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);
        seatGroupDTO.getEventSeatGroups().add(esgDTO2);

        ResponseEntity<EventDTO> result = testRestTemplate.exchange(createURLWithPort("/events/location"),
                HttpMethod.PUT, new HttpEntity<>(seatGroupDTO), EventDTO.class);

        EventDTO eventDTO = result.getBody();
        Event event = eventRepository.getOne(eventID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(eventDTO);
        assertEquals(locationID, event.getLocation().getId());
        assertEquals(2, event.getEventSeatGroups().size());
    }

    @Test
    void setEventLocationAndSeatGroups_eventDoesNotExist_errorMessageReturned() {
        Long eventID = 31L;
        Long locationID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(1L);
        EventSeatGroupDTO esgDTO2 = new EventSeatGroupDTO(26L);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);
        seatGroupDTO.getEventSeatGroups().add(esgDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/location"),
                HttpMethod.PUT, new HttpEntity<>(seatGroupDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", errorMessage);
    }

    @Test
    void setEventLocationAndSeatGroups_eventExistsAndLocationDoesNotExist_errorMessageReturned() {
        Long eventID = 1L;
        Long locationID = 31L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(1L);
        EventSeatGroupDTO esgDTO2 = new EventSeatGroupDTO(26L);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);
        seatGroupDTO.getEventSeatGroups().add(esgDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/location"),
                HttpMethod.PUT, new HttpEntity<>(seatGroupDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", errorMessage);
    }

    @Test
    void setEventLocationAndSeatGroups_locationIsAlreadyTaken_errorMessageReturned() {
        Long eventID = 1L;
        Long locationID = 2L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(2L);
        EventSeatGroupDTO esgDTO2 = new EventSeatGroupDTO(27L);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);
        seatGroupDTO.getEventSeatGroups().add(esgDTO2);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/location"),
                HttpMethod.PUT, new HttpEntity<>(seatGroupDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Location cannot be changed if reservation for event exist", errorMessage);
    }

    @Test
    void setEventLocationAndSeatGroups_seatGroupAlreadyHasReservation_errorMessageReturned() {
        Long eventID = 1L;
        Long locationID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(1L);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/events/location"),
                HttpMethod.PUT, new HttpEntity<>(seatGroupDTO), String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Seat group which has at least one reservation cannot be disabled", errorMessage);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }
}

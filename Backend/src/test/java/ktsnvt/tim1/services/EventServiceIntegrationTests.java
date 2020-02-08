package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.repositories.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventServiceIntegrationTests {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void getEvents_pageRequestSent_eventsReturned() {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<EventDTO> page = eventService.getEvents(pageable);

        long totalEventCount = eventRepository.count();

        if (totalEventCount < pageSize)
            assertTrue(page.getTotalElements() < pageSize);
        else
            assertEquals(pageSize, page.getSize());
    }

    @Test
    void getEvent_eventExists_eventReturned() throws EntityNotFoundException {
        Long id = 1L;

        EventDTO event = eventService.getEvent(id);

        assertEquals(id, event.getId());
    }

    @Test
    void getEvent_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;
        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(id));
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_eventCreated() throws EntityNotValidException, EntityAlreadyExistsException {
        EventDTO newDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.getEventDays().add(new EventDayDTO(null, "24.02.2020. 12:30"));
        newDTO.setReservationDeadlineDays(2);

        EventDTO newEventSaved = eventService.createEvent(newDTO);

        assertEquals(newDTO.getName(), newEventSaved.getName());
        assertEquals(newDTO.getDescription(), newEventSaved.getDescription());
        assertEquals(newDTO.getCategory(), newEventSaved.getCategory());
        assertEquals(newDTO.isCancelled(), newEventSaved.isCancelled());
        assertNotEquals(null, newEventSaved.getId());
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_eventDayNotValid_entityNotValidExceptionThrown() {
        EventDTO eventDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(null, "30.11.2019 12:30"));

        assertThrows(EntityNotValidException.class, () -> eventService.createEvent(eventDTO));
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_eventDayDateIsBeforeTodayDate_entityNotValidExceptionThrown() {
        EventDTO eventDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(null, "30.11.2019. 12:30"));

        assertThrows(EntityNotValidException.class, () -> eventService.createEvent(eventDTO));
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_reservationDeadlineDaysInvalid_entityNotValidExceptionThrown() {
        EventDTO eventDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(null, "28.02.2020. 12:30"));
        eventDTO.setReservationDeadlineDays(24);

        assertThrows(EntityNotValidException.class, () -> eventService.createEvent(eventDTO));
    }

    @Transactional
    @Rollback
    @Test
    void createEvent_eventNameIsTaken_entityAlreadyExistsExceptionThrown() {
        EventDTO eventDTO = new EventDTO(null, "Conputor", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(null, "28.02.2019. 12:30"));
        eventDTO.setReservationDeadlineDays(2);

        assertThrows(EntityAlreadyExistsException.class, () -> eventService.createEvent(eventDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventExists_eventEdited() throws EntityNotFoundException, EntityAlreadyExistsException, EntityNotValidException {
        EventDTO newDTO = new EventDTO(5L, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setReservationDeadlineDays(5);
        LocalDateTime eventDayDate = LocalDateTime.now().plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        EventDayDTO eventDayDTO1 = new EventDayDTO(51L, formatter.format(eventDayDate));
        newDTO.getEventDays().add(eventDayDTO1);

        EventDTO newEventSaved = eventService.editEvent(newDTO);

        assertEquals(newDTO.getId(), newEventSaved.getId());
        assertEquals(newDTO.getName(), newEventSaved.getName());
        assertEquals(newDTO.getDescription(), newEventSaved.getDescription());
        assertEquals(newDTO.getCategory(), newEventSaved.getCategory());
        assertEquals(newDTO.isCancelled(), newEventSaved.isCancelled());
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventIdIsNull_entityNotValidExceptionThrown() {
        EventDTO newDTO = new EventDTO(1L, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        assertThrows(EntityNotValidException.class, () -> eventService.editEvent(newDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;
        EventDTO newDTO = new EventDTO(id, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);

        assertThrows(EntityNotFoundException.class, () -> eventService.editEvent(newDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventNameIsTaken_entityAlreadyExistsExceptionThrown() {
        Long id = 2L;
        EventDTO newDTO = new EventDTO(id, "Conputor", "Description of event",
                EventCategory.Movie.name(), false);

        assertThrows(EntityAlreadyExistsException.class, () -> eventService.editEvent(newDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editEvent_eventDayDateIsBeforeTodayDate_entityNotValidExceptionThrown() {
        EventDTO eventDTO = new EventDTO(1L, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(1L, "30.11.2019. 12:30"));

        assertThrows(EntityNotValidException.class, () -> eventService.editEvent(eventDTO));
    }

    @Transactional
    @Rollback
    @Test
    public void uploadPicturesAndVideos_eventExists_picturesAndVideosUploaded() throws EntityNotValidException, EntityNotFoundException {
        Random r = new Random();

        byte[] image = new byte[20];
        r.nextBytes(image);
        MultipartFile mf1 = new MockMultipartFile("img.jpg", "img.jpg", "image/jpeg", image);

        byte[] video = new byte[20];
        r.nextBytes(video);
        MultipartFile mf2 = new MockMultipartFile("video.mp4", "video.mp4", "video/mp4", video);

        MultipartFile[] files = new MultipartFile[2];
        files[0] = mf1;
        files[1] = mf2;

        eventService.uploadPicturesAndVideos(5L, files);

        Optional<Event> eventOptional = eventRepository.findByIdAndIsCancelledFalseAndLocationNotNull(5L);
        Event e = null;
        if (eventOptional.isPresent())
            e = eventOptional.get();

        assertNotNull(e);
        assertEquals(4, e.getPicturesAndVideos().size());
    }

    @Test
    void uploadPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;

        assertThrows(EntityNotFoundException.class, () -> eventService.uploadPicturesAndVideos(id, new MultipartFile[2]));
    }

    @Test
    void uploadPicturesAndVideos_fileContentTypeNotValid_entityNotValidExceptionThrown() {
        Random r = new Random();

        byte[] txt = new byte[20];
        r.nextBytes(txt);
        MultipartFile mf1 = new MockMultipartFile("1.txt", "1.txt", "text/plain", txt);

        MultipartFile[] files = new MultipartFile[1];
        files[0] = mf1;

        assertThrows(EntityNotValidException.class, () -> eventService.uploadPicturesAndVideos(1L, files));
    }

    @Transactional
    @Test
    void getPicturesAndVideos_eventExists_picturesAndVideosReturned() throws EntityNotFoundException {
        Set<MediaFileDTO> files = eventService.getPicturesAndVideos(4L);

        assertEquals(2, files.size());
    }

    @Test
    void getPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;

        assertThrows(EntityNotFoundException.class, () -> eventService.getPicturesAndVideos(id));
    }

    @Transactional
    @Rollback
    @Test
    void deleteMediaFile_eventExistsAndMediaFileExists_mediaFileDeleted() throws EntityNotFoundException {
        Long eventID = 6L;
        Long fileID = 31L;

        eventService.deleteMediaFile(eventID, fileID);

        Optional<Event> eventOptional = eventRepository.findByIdAndIsCancelledFalseAndLocationNotNull(eventID);
        Event e = null;
        if (eventOptional.isPresent())
            e = eventOptional.get();

        assertNotNull(e);
        assertEquals(1, e.getPicturesAndVideos().size());
    }

    @Test
    void deleteMediaFile_eventExistsAndMediaFileDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long fileID = 51L;

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteMediaFile(eventID, fileID));
    }

    @Test
    void deleteMediaFile_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 31L;
        Long fileID = 26L;

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteMediaFile(eventID, fileID));
    }

    @Transactional
    @Rollback
    @Test
    public void setEventLocationAndSeatGroups_eventExistsAndLocationExists_seatGroupSet() throws Exception {
        Long eventID = 1L;
        Long locationID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO1 = new EventSeatGroupDTO(1L);
        EventSeatGroupDTO esgDTO2 = new EventSeatGroupDTO(26L);
        seatGroupDTO.getEventSeatGroups().add(esgDTO1);
        seatGroupDTO.getEventSeatGroups().add(esgDTO2);

        eventService.setEventLocationAndSeatGroups(seatGroupDTO);

        Optional<Event> eventOptional = eventRepository.findByIdAndIsCancelledFalseAndLocationNotNull(eventID);
        Event event = null;
        if (eventOptional.isPresent())
            event = eventOptional.get();

        assertNotNull(event);
        assertEquals(locationID, event.getLocation().getId());
        assertEquals(2, event.getEventSeatGroups().size());
    }

    @Test
    void setEventLocationAndSeatGroups_eventExistsAndLocationDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long locationID = 31L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);

        assertThrows(EntityNotFoundException.class, () -> eventService.setEventLocationAndSeatGroups(seatGroupDTO));
    }

    @Test
    void setEventLocationAndSeatGroups_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 31L;
        Long locationID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);

        assertThrows(EntityNotFoundException.class, () -> eventService.setEventLocationAndSeatGroups(seatGroupDTO));
    }

    @Test
    void searchEvents_searchParametersProvided_pageReturned() throws EntityNotValidException {
        SearchEventsDTO searchDTO = new SearchEventsDTO("", null, null,
                "07.01.2020. 12:30", "13.01.2020. 13:30");
        Pageable pageable = PageRequest.of(0, 5);

        Page<EventDTO> page = eventService.searchEvents(searchDTO, pageable);

        assertEquals(3, page.getNumberOfElements());
    }

    @Test
    void searchEvents_searchParametersEmpty_pageReturned() throws EntityNotValidException {
        SearchEventsDTO searchDTO = new SearchEventsDTO("", null, null,
                "", "");
        Pageable pageable = PageRequest.of(0, 5);

        Page<EventDTO> page = eventService.searchEvents(searchDTO, pageable);

        long totalEventsCount = eventRepository.count();
        assertEquals(totalEventsCount, page.getTotalElements());
    }

    @Test
    void searchEvents_searchParametersDoesNotMatchAnyEvent_emptyPageReturned() throws EntityNotValidException {
        Pageable pageable = PageRequest.of(0, 5);
        SearchEventsDTO searchDTO = new SearchEventsDTO("dsadahfghbfghvcs", null, null,
                "", "");

        Page<EventDTO> page = eventService.searchEvents(searchDTO, pageable);

        assertTrue(page.isEmpty());
    }

    @Test
    void searchEvents_searchDatesAreInvalid_entityNotValidExceptionThrown() {
        SearchEventsDTO searchDTO = new SearchEventsDTO("", null, null,
                "07.2020. 12:30", "15.2020. 13:30");
        Pageable pageable = PageRequest.of(0, 5);

        assertThrows(EntityNotValidException.class, () -> eventService.searchEvents(searchDTO, pageable));
    }

    @Test
    void getEventsOptions_optionsReturned() {
        int numberOfEvents = 25;

        List<EventOptionDTO> eventOptions = eventService.getEventsOptions();

        assertNotNull(eventOptions);
        assertEquals(numberOfEvents, eventOptions.size());
    }

    @Test
    void getEventLocationAndSeatGroups_eventExists_locationSeatGroupDTOReturned() throws EntityNotFoundException {
        LocationSeatGroupDTO lsgDTO = eventService.getEventLocationAndSeatGroups(2L);

        assertNotNull(lsgDTO);
        assertEquals(2, lsgDTO.getEventSeatGroups().size());
    }

    @Test
    void getEventLocationAndSeatGroups_eventExists_entityNotFoundExceptionThrown() {
        assertThrows(EntityNotFoundException.class, () -> eventService.getEventLocationAndSeatGroups(52L));
    }
}

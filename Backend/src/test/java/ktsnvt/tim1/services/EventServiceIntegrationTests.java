package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.model.EventSeatGroup;
import ktsnvt.tim1.model.MediaFile;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.MediaFileRepository;
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

import javax.transaction.Transactional;

import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventServiceIntegrationTests {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    public void getEvents_pageRequestSent_eventsReturned() {
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
    public void getEvent_eventExists_eventReturned() throws EntityNotFoundException {
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
    void createEvent_eventCreated() throws EntityNotValidException {
        EventDTO newDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);

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
    public void editEvent_eventExists_eventEditedAndReturned() throws EntityNotFoundException, EntityAlreadyExistsException, EntityNotValidException {
        EventDTO newDTO = new EventDTO(1L, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        newDTO.setReservationDeadlineDays(5);
        EventDayDTO eventDayDTO1 = new EventDayDTO(1L, "01.01.2020. 00:00");
        EventDayDTO eventDayDTO2 = new EventDayDTO(26L, "20.02.2020. 00:00");
        newDTO.getEventDays().add(eventDayDTO1);
        newDTO.getEventDays().add(eventDayDTO2);

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

        eventService.uploadPicturesAndVideos(1L, files);

        assertEquals(4, eventRepository.findByIdAndIsCancelledFalse(1L).get().getPicturesAndVideos().size());
    }

    @Test
    public void uploadPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;

        assertThrows(EntityNotFoundException.class, () -> eventService.uploadPicturesAndVideos(id, new MultipartFile[2]));
    }

    @Test
    public void uploadPicturesAndVideos_fileContentTypeNotValid_entityNotValidExceptionThrown() {
        Random r = new Random();

        byte[] txt = new byte[20];
        r.nextBytes(txt);
        MultipartFile mf1 = new MockMultipartFile("1.txt", "1.txt", "text/plain", txt);

        MultipartFile[] files = new MultipartFile[1];
        files[0] = mf1;

        assertThrows(EntityNotValidException.class, () -> eventService.uploadPicturesAndVideos(1L, files));
    }

    @Test
    void getPicturesAndVideos_eventExists_picturesAndVideosReturned() throws EntityNotFoundException {
        Set<MediaFile> files = eventService.getPicturesAndVideos(1L);

        assertEquals(2, files.size());
    }


    @Test
    public void getPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;

        assertThrows(EntityNotFoundException.class, () -> eventService.getPicturesAndVideos(id));
    }

    @Transactional
    @Rollback
    @Test
    public void deleteMediaFile_eventExistsAndMediaFileExists_mediaFileDeleted() throws EntityNotFoundException {
        Long eventID = 1L;
        Long fileID = 26L;

        eventService.deleteMediaFile(eventID, fileID);

        assertEquals(1, eventRepository.findByIdAndIsCancelledFalse(eventID).get().getPicturesAndVideos().size());
    }

    @Test
    public void deleteMediaFile_eventExistsAndMediaFileDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long fileID = 51L;

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteMediaFile(eventID, fileID));
    }

    @Test
    public void deleteMediaFile_eventDoesNotExist_entityNotFoundExceptionThrown() {
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

        Event event = eventRepository.findByIdAndIsCancelledFalse(eventID).get();

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
}

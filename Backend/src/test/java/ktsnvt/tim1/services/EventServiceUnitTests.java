package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.EventDayMapper;
import ktsnvt.tim1.mappers.EventMapper;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.MediaFileRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest(EventService.class)
@ActiveProfiles("test")
public class EventServiceUnitTests {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepositoryMocked;

    @MockBean
    private LocationRepository locationRepositoryMocked;

    @MockBean
    private MediaFileRepository mediaFileRepositoryMocked;

    @MockBean
    private EventMapper eventMapperMocked;

    @MockBean
    private EventDayMapper eventDayMapperMocked;

    private static DateTimeFormatter formatter;

    @BeforeClass
    public static void setUpDateTimeFormatter() {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    }


    @Test
    public void getEvents_repositoryMethodCalledOnce() {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito.when(eventRepositoryMocked.findAll(pageable)).thenReturn(Page.empty());
        eventService.getEvents(pageable);
        verify(eventRepositoryMocked, times(1)).findAll(pageable);
    }

    @Test
    public void getEvent_eventExists_eventReturned() throws EntityNotFoundException {
        Long id = 1L;
        Event entity = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);

        EventDTO returnDTO = new EventDTO(id, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        Optional<Event> o = Optional.of(entity);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(entity.getId())).thenReturn(o);
        Mockito.when(eventMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        EventDTO event = eventService.getEvent(id);

        assertEquals(id, event.getId());
        verify(eventRepositoryMocked, times(1)).findByIdAndIsCancelledFalse(id);
    }

    @Test
    public void getEvent_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;
        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(id));
    }

    @Test
    public void createEvent_eventCreated() throws EntityNotValidException {
        Long id = 1L;
        EventDTO dtoBeforeSaving = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);

        Event eventBeforeSaving = new Event(null, dtoBeforeSaving.getName(), dtoBeforeSaving.getDescription(),
                EventCategory.valueOf(dtoBeforeSaving.getCategory()), dtoBeforeSaving.isCancelled());

        EventDTO dtoAfterSaving = new EventDTO(id, dtoBeforeSaving.getName(), dtoBeforeSaving.getDescription(),
                dtoBeforeSaving.getCategory(), dtoBeforeSaving.isCancelled());

        Event eventAfterSaving = new Event(id, dtoBeforeSaving.getName(), dtoBeforeSaving.getDescription(),
                EventCategory.valueOf(dtoBeforeSaving.getCategory()), dtoBeforeSaving.isCancelled());

        Mockito.when(eventMapperMocked.toEntity(dtoBeforeSaving)).thenReturn(eventBeforeSaving);
        Mockito.when(eventRepositoryMocked.save(eventBeforeSaving)).thenReturn(eventAfterSaving);
        Mockito.when(eventMapperMocked.toDTO(eventAfterSaving)).thenReturn(dtoAfterSaving);

        EventDTO eventDTO = eventService.createEvent(dtoBeforeSaving);

        assertEquals(id, eventDTO.getId());
        verify(eventMapperMocked, times(1)).toEntity(dtoBeforeSaving);
        verify(eventRepositoryMocked, times(1)).save(eventBeforeSaving);
        verify(eventMapperMocked, times(1)).toDTO(eventAfterSaving);
    }

    @Test
    public void createEvent_eventDayNotValid_entityNotValidExceptionThrown() throws EntityNotValidException {
        EventDTO eventDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        eventDTO.getEventDays().add(new EventDayDTO(null, "30.11.2019 12:30"));

        Mockito.when(eventMapperMocked.toEntity(eventDTO)).thenThrow(EntityNotValidException.class);

        assertThrows(EntityNotValidException.class, () -> eventService.createEvent(eventDTO));
    }

    @Test
    public void editEvent_eventExists_eventReturned() throws Exception {
        Long id = 1L;
        EventService eventServiceSpy = PowerMockito.spy(eventService);

        Event oldEntity = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);

        EventDTO editedDTO = new EventDTO(id, "Event 2", oldEntity.getDescription(),
                oldEntity.getCategory().name(), oldEntity.getCancelled());

        Event newEntity = new Event(id, editedDTO.getName(), editedDTO.getDescription(),
                EventCategory.valueOf(editedDTO.getCategory()), editedDTO.isCancelled());

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.of(oldEntity));
        Mockito.when(eventRepositoryMocked.findOneByName(oldEntity.getName())).thenReturn(oldEntity);
        Mockito.when(eventRepositoryMocked.save(oldEntity)).thenReturn(newEntity);
        Mockito.when(eventMapperMocked.toDTO(newEntity)).thenReturn(editedDTO);
        PowerMockito.doNothing().when(eventServiceSpy, "addAndRemoveEventDays", oldEntity, editedDTO);
        PowerMockito.doNothing().when(eventServiceSpy, "checkNumberOfReservationDeadlineDays", oldEntity, editedDTO);

        EventDTO editedEvent = eventServiceSpy.editEvent(editedDTO);

        assertEquals(editedDTO.getId(), editedEvent.getId());
        assertEquals(editedDTO.getName(), editedEvent.getName());
        assertEquals(editedDTO.getDescription(), editedEvent.getDescription());
        assertEquals(editedDTO.getCategory(), editedEvent.getCategory());

        verify(eventRepositoryMocked, times(1)).findByIdAndIsCancelledFalse(id);
        verify(eventRepositoryMocked, times(1)).findOneByName(oldEntity.getName());
        verify(eventRepositoryMocked, times(1)).save(oldEntity);
        verify(eventMapperMocked, times(1)).toDTO(newEntity);
        PowerMockito.verifyPrivate(eventServiceSpy, times(1)).invoke("addAndRemoveEventDays", oldEntity, editedDTO);
        PowerMockito.verifyPrivate(eventServiceSpy, times(1))
                .invoke("checkNumberOfReservationDeadlineDays", oldEntity, editedDTO);
    }

    @Test
    public void editEvent_eventIdIsNull_entityNotValidExceptionThrown() {
        EventDTO editedDTO = new EventDTO(null, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);
        assertThrows(EntityNotValidException.class, () -> eventService.editEvent(editedDTO));
    }

    @Test
    public void editEvent_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;
        EventDTO editedDTO = new EventDTO(id, "Event 1", "Description of Event 1",
                EventCategory.Movie.name(), false);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.editEvent(editedDTO));
    }

    @Test
    public void editEvent_eventNameIsTaken_entityAlreadyExistsExceptionThrown() {
        Long id = 1L;
        Long eventID = 2L;
        EventDTO editedDTO = new EventDTO(id, "Event 2", "Description",
                EventCategory.Sport.name(), false);
        Event eventToEdit = new Event(id, "Event 1", editedDTO.getDescription(),
                EventCategory.valueOf(editedDTO.getCategory()), editedDTO.isCancelled());
        Event event = new Event(eventID, "Event 2", "Description of Event 2",
                EventCategory.Movie, false);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.of(eventToEdit));
        Mockito.when(eventRepositoryMocked.findOneByName(editedDTO.getName())).thenReturn(event);

        assertThrows(EntityAlreadyExistsException.class, () -> eventService.editEvent(editedDTO));
    }

    @Test
    public void uploadPicturesAndVideos_eventExists_picturesAndVideosUploaded() throws EntityNotValidException, EntityNotFoundException {
        Long id = 1L;
        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
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

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.of(event));
        Mockito.when(eventRepositoryMocked.save(event)).thenReturn(event);

        eventService.uploadPicturesAndVideos(id, files);

        assertEquals(2, event.getPicturesAndVideos().size());
    }

    @Test
    public void uploadPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService
                .uploadPicturesAndVideos(id, new MultipartFile[2]));
    }

    @Test
    public void uploadPicturesAndVideos_fileContentTypeNotValid_entityNotValidExceptionThrown() {
        Long id = 1L;
        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        Random r = new Random();

        byte[] txt = new byte[20];
        r.nextBytes(txt);
        MultipartFile mf1 = new MockMultipartFile("1.txt", "1.txt", "text/plain", txt);

        MultipartFile[] files = new MultipartFile[1];
        files[0] = mf1;

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.of(event));
        Mockito.when(eventRepositoryMocked.save(event)).thenReturn(event);

        assertThrows(EntityNotValidException.class, () -> eventService.uploadPicturesAndVideos(id, files));
    }

    @Test
    public void getPicturesAndVideos_eventExists_picturesAndVideosReturned() throws EntityNotFoundException {
        Long id = 1L;
        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        Random r = new Random();

        byte[] image = new byte[20];
        r.nextBytes(image);
        event.getPicturesAndVideos().add(new MediaFile("img.jpg", "image/jpeg", image));

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.of(event));

        Set<MediaFile> files = eventService.getPicturesAndVideos(id);

        assertEquals(1, files.size());
    }

    @Test
    public void getPicturesAndVideos_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getPicturesAndVideos(id));
    }

    @Test
    public void deleteMediaFile_eventExistsAndMediaFileExists_mediaFileDeleted() throws EntityNotFoundException {
        Long eventID = 1L;
        Long fileID = 2L;
        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        Random r = new Random();

        byte[] image = new byte[20];
        r.nextBytes(image);
        MediaFile mf = new MediaFile("img.jpg", "image/jpeg", image);
        mf.setId(fileID);
        event.getPicturesAndVideos().add(mf);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.of(event));
        Mockito.when(mediaFileRepositoryMocked.findById(fileID)).thenReturn(Optional.of(mf));

        eventService.deleteMediaFile(eventID, fileID);

        assertTrue(event.getPicturesAndVideos().isEmpty());
    }

    @Test
    public void deleteMediaFile_eventExistsAndMediaFileDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long fileID = 2L;
        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.of(event));
        Mockito.when(mediaFileRepositoryMocked.findById(fileID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteMediaFile(eventID, fileID));
    }

    @Test
    public void deleteMediaFile_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long fileID = 2L;

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteMediaFile(eventID, fileID));
    }

    @Test
    public void setEventLocationAndSeatGroups_eventExistsAndLocationExists_privateMethodsCalledOnce() throws Exception {
        Long eventID = 1L;
        Long locationID = 2L;
        EventService eventServiceSpy = PowerMockito.spy(eventService);

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.setLocation(new Location());

        EventDTO eventDTO = new EventDTO(eventID, event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());

        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);

        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO(eventID, locationID);

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.of(event));
        Mockito.when(locationRepositoryMocked.findByIdAndDisabledFalse(locationID)).thenReturn(Optional.of(location));
        Mockito.when(eventRepositoryMocked.save(event)).thenReturn(event);
        Mockito.when(eventMapperMocked.toDTO(event)).thenReturn(eventDTO);
        PowerMockito.doNothing().when(eventServiceSpy, "changeLocation", event, location);
        PowerMockito.doNothing().when(eventServiceSpy, "disableEventGroups", event, seatGroupDTO);
        PowerMockito.doNothing().when(eventServiceSpy, "enableEventGroups", event, location, seatGroupDTO);

        eventServiceSpy.setEventLocationAndSeatGroups(seatGroupDTO);

        verify(eventRepositoryMocked, times(1)).save(event);
        verify(eventMapperMocked, times(1)).toDTO(event);
        PowerMockito.verifyPrivate(eventServiceSpy, times(1)).invoke("changeLocation", event, location);
        PowerMockito.verifyPrivate(eventServiceSpy, times(1)).invoke("disableEventGroups", event, seatGroupDTO);
        PowerMockito.verifyPrivate(eventServiceSpy, times(1))
                .invoke("enableEventGroups", event, location, seatGroupDTO);
    }

    @Test
    public void setEventLocationAndSeatGroups_eventExistsAndLocationDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        Long fileID = 2L;
        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO();

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.of(event));
        Mockito.when(locationRepositoryMocked.findByIdAndDisabledFalse(fileID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.setEventLocationAndSeatGroups(seatGroupDTO));
    }

    @Test
    public void setEventLocationAndSeatGroups_eventDoesNotExist_entityNotFoundExceptionThrown() {
        Long eventID = 1L;
        LocationSeatGroupDTO seatGroupDTO = new LocationSeatGroupDTO();

        Mockito.when(eventRepositoryMocked.findByIdAndIsCancelledFalse(eventID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.setEventLocationAndSeatGroups(seatGroupDTO));
    }

    @Test
    public void searchEvents_pageReturned() throws EntityNotValidException {
        Long id = 1L;
        Long eventDayID = 2L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(eventDayID, LocalDateTime.parse("02.12.2019. 15:00", formatter)));

        EventDTO eventDTO = new EventDTO(id, event.getName(), event.getDescription(),
                EventCategory.Movie.name(), event.getCancelled());

        SearchEventsDTO searchDTO = new SearchEventsDTO("", null, null,
                "01.12.2019. 12:30", "03.12.2019. 13:30");
        Pageable pageable = PageRequest.of(0, 5);

        ArrayList<EventDTO> eventDTOs = new ArrayList<>();
        eventDTOs.add(eventDTO);
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        Page<EventDTO> expectedPage = new PageImpl<>(eventDTOs);
        Page<Event> returnPageEvents = new PageImpl<>(events);

        Mockito.when(eventRepositoryMocked.searchEvents("%", null,
                searchDTO.getLocationID(), Pageable.unpaged())).thenReturn(returnPageEvents);
        Mockito.when(eventMapperMocked.toDTO(event)).thenReturn(eventDTO);

        Page<EventDTO> returnedPage = eventService.searchEvents(searchDTO, pageable);

        assertEquals(expectedPage.getNumberOfElements(), returnedPage.getNumberOfElements());
        verify(eventMapperMocked, times(1)).toDTO(event);
    }

    @Test
    public void searchEvents_searchDatesAreInvalid_entityNotValidExceptionThrown() {
        Long id = 1L;
        Long eventDayID = 2L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(eventDayID, LocalDateTime.parse("02.12.2019. 15:00", formatter)));

        SearchEventsDTO searchDTO = new SearchEventsDTO("", null, null,
                "01.2019. 12:30", "03.2019. 13:30");
        Pageable pageable = PageRequest.of(0, 5);

        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        Page<Event> returnPageEvents = new PageImpl<>(events);

        Mockito.when(eventRepositoryMocked.searchEvents("%", null,
                searchDTO.getLocationID(), pageable)).thenReturn(returnPageEvents);

        assertThrows(EntityNotValidException.class, () -> eventService.searchEvents(searchDTO, pageable));
    }

    @Test
    public void addAndRemoveEventDays_eventDaysAddedAndRemoved() throws Exception {
        Long id = 1L;
        Long toRemoveEventDayID = 2L;
        Long toAddEventDayID = 3L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(toRemoveEventDayID, LocalDateTime.parse("06.06.2020. 15:00", formatter)));
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        event.getEventDays().iterator().next().getReservableSeatGroups().add(rsg);

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());
        EventDayDTO eventDayToAddDTO = new EventDayDTO(toAddEventDayID, "07.06.2020. 22:00");
        EventDay eventDayToAdd = new EventDay(toAddEventDayID, LocalDateTime.parse("07.06.2020. 22:00", formatter));
        eventDTO.getEventDays().add(eventDayToAddDTO);

        Mockito.when(eventDayMapperMocked.toEntity(eventDayToAddDTO)).thenReturn(eventDayToAdd);

        Whitebox.invokeMethod(eventService, "addAndRemoveEventDays", event, eventDTO);

        assertEquals(1, event.getEventDays().size());
        assertEquals("07.06.2020. 22:00", formatter.format(event.getEventDays().iterator().next().getDate()));
    }

    @Test
    public void addAndRemoveEventDays_removeEventDayWhichHasReservation_entityNotValidExceptionThrown() {
        Long id = 1L;
        Long toRemoveEventDayID = 2L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(toRemoveEventDayID, LocalDateTime.parse("06.06.2020. 15:00", formatter)));
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        Ticket ticket = new Ticket();
        rsg.getTickets().add(ticket);
        event.getEventDays().iterator().next().getReservableSeatGroups().add(rsg);

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());

        assertThrows(EntityNotValidException.class, () -> Whitebox
                .invokeMethod(eventService, "addAndRemoveEventDays", event, eventDTO));
    }

    @Test
    public void addAndRemoveEventDays_eventDayDateIsInvalid_entityNotValidExceptionThrown() throws EntityNotValidException {
        Long id = 1L;
        Long toAddEventDayID = 3L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());
        EventDayDTO eventDayToAddDTO = new EventDayDTO(toAddEventDayID, "07.2020. 22:00");
        eventDTO.getEventDays().add(eventDayToAddDTO);

        Mockito.when(eventDayMapperMocked.toEntity(eventDayToAddDTO)).thenThrow(EntityNotValidException.class);

        assertThrows(EntityNotValidException.class, () -> Whitebox
                .invokeMethod(eventService, "addAndRemoveEventDays", event, eventDTO));
    }

    @Test
    public void checkNumberOfReservationDeadlineDays_reservationDeadlineDaysSet() throws Exception {
        Long id = 1L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(id, LocalDateTime.parse("06.12.2019. 15:00", formatter)));

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());
        eventDTO.setReservationDeadlineDays(5);

        LocalDateTime testDate = LocalDateTime.parse("30.11.2019. 12:30", formatter);
        PowerMockito.mockStatic(LocalDateTime.class);
        PowerMockito.when(LocalDateTime.now()).thenReturn(testDate);

        Whitebox.invokeMethod(eventService, "checkNumberOfReservationDeadlineDays", event, eventDTO);

        assertEquals(5, event.getReservationDeadlineDays().intValue());
    }

    @Test
    public void checkNumberOfReservationDeadlineDays_reservationDeadlineDaysInvalid_entityNotValidExceptionThrown() {
        Long id = 1L;

        Event event = new Event(id, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.getEventDays().add(new EventDay(id, LocalDateTime.parse("06.12.2019. 15:00", formatter)));

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDescription(),
                event.getCategory().name(), event.getCancelled());
        eventDTO.setReservationDeadlineDays(6);

        assertThrows(EntityNotValidException.class, () -> Whitebox
                .invokeMethod(eventService, "checkNumberOfReservationDeadlineDays", event, eventDTO));
    }

    @Test
    public void changeLocation_eventsLocationChanged() throws Exception {
        Long eventID = 1L;
        Long locationID = 2L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.setLocation(new Location());

        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);

        Whitebox.invokeMethod(eventService, "changeLocation", event, location);

        assertEquals(location, event.getLocation());
    }

    @Test
    public void changeLocation_locationAlreadyTaken_entityNotValidExceptionThrown() {
        Long eventID = 1L;
        Long locationID = 2L;
        Long locationsEventID = 3L;
        Long eventDayID = 4L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        EventDay eventDay = new EventDay(eventDayID, LocalDateTime.parse("07.06.2020. 22:00", formatter));
        event.setLocation(new Location());
        event.getEventDays().add(eventDay);

        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);
        Event locationsEvent = new Event(locationsEventID, "Event 2", "Description of Event 2",
                EventCategory.Sport, false);
        locationsEvent.getEventDays().add(eventDay);
        location.getEvents().add(locationsEvent);

        assertThrows(EntityNotValidException.class, () -> Whitebox
                .invokeMethod(eventService, "changeLocation", event, location));
    }

    @Test
    public void changeLocation_eventHasReservation_entityNotValidExceptionThrown() {
        Long eventID = 1L;
        Long locationID = 2L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.setLocation(new Location());
        EventSeatGroup esg = new EventSeatGroup();
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        Ticket ticket = new Ticket();
        rsg.getTickets().add(ticket);
        esg.getReservableSeatGroups().add(rsg);
        event.getEventSeatGroups().add(esg);

        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);

        assertThrows(EntityNotValidException.class, () -> Whitebox
                .invokeMethod(eventService, "changeLocation", event, location));
    }

    @Test
    public void disableEventGroups_eventGroupsDisabled() throws Exception {
        Long eventID = 1L;
        Long locationID = 2L;
        Long firstEventSeatGroupID = 3L;
        Long secondEventSeatGroupID = 4L;
        Long seatGroupID = 5L;
        Long toDisableSeatGroupID = 6L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        event.setLocation(new Location());
        EventSeatGroup esg1 = new EventSeatGroup(firstEventSeatGroupID);
        esg1.setSeatGroup(new SeatGroup(seatGroupID));
        EventSeatGroup esg2 = new EventSeatGroup(secondEventSeatGroupID);
        esg2.setSeatGroup(new SeatGroup(toDisableSeatGroupID));
        ReservableSeatGroup rsg1 = new ReservableSeatGroup();
        ReservableSeatGroup rsg2 = new ReservableSeatGroup();
        esg1.getReservableSeatGroups().add(rsg1);
        esg2.getReservableSeatGroups().add(rsg2);
        event.getEventSeatGroups().add(esg1);
        event.getEventSeatGroups().add(esg2);

        LocationSeatGroupDTO lsg = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO(seatGroupID);
        lsg.getEventSeatGroups().add(esgDTO);

        Whitebox.invokeMethod(eventService, "disableEventGroups", event, lsg);

        assertEquals(1, event.getEventSeatGroups().size());
        assertEquals(firstEventSeatGroupID, event.getEventSeatGroups().iterator().next().getId());
    }

    @Test
    public void disableEventGroups_eventSeatGroupHasReservation_entityNotValidExceptionThrown() {
        Long eventID = 1L;
        Long locationID = 2L;
        Long eventSeatGroupID = 3L;
        Long seatGroupID = 4L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        EventSeatGroup esg = new EventSeatGroup(eventSeatGroupID);
        esg.setSeatGroup(new SeatGroup(eventSeatGroupID));
        ReservableSeatGroup rsg = new ReservableSeatGroup();
        Ticket ticket = new Ticket();
        rsg.getTickets().add(ticket);
        esg.getReservableSeatGroups().add(rsg);
        event.getEventSeatGroups().add(esg);

        LocationSeatGroupDTO lsg = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO(seatGroupID);
        lsg.getEventSeatGroups().add(esgDTO);

        assertThrows(EntityNotValidException.class, ()
                -> Whitebox.invokeMethod(eventService, "disableEventGroups", event, lsg));
    }

    @Test
    public void enableSeatGroups_seatGroupIsNotEnabled_seatGroupEnabled() throws Exception {
        Long eventID = 1L;
        Long locationID = 2L;
        Long seatGroupID = 3L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);
        location.getSeatGroups().add(new SeatGroup(seatGroupID));

        LocationSeatGroupDTO lsg = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO(seatGroupID);
        lsg.getEventSeatGroups().add(esgDTO);

        Whitebox.invokeMethod(eventService, "enableEventGroups", event, location, lsg);

        assertEquals(1, event.getEventSeatGroups().size());
    }

    @Test
    public void enableSeatGroups_seatGroupIsEnabled_seatGroupPriceChanged() throws Exception {
        Long eventID = 1L;
        Long locationID = 2L;
        Long seatGroupID = 3L;

        Event event = new Event(eventID, "Event 1", "Description of Event 1",
                EventCategory.Movie, false);
        SeatGroup sg = new SeatGroup(seatGroupID);
        EventSeatGroup esg = new EventSeatGroup();
        esg.setPrice(200.0);
        esg.setSeatGroup(sg);
        event.getEventSeatGroups().add(esg);
        Location location = new Location(locationID, "Spens", 50.0, 50.0, false);
        location.getSeatGroups().add(sg);

        LocationSeatGroupDTO lsg = new LocationSeatGroupDTO(eventID, locationID);
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO(seatGroupID);
        esgDTO.setPrice(300.0);
        lsg.getEventSeatGroups().add(esgDTO);

        Whitebox.invokeMethod(eventService, "enableEventGroups", event, location, lsg);

        assertEquals(300.0, event.getEventSeatGroups().iterator().next().getPrice(), 0.0);
    }
}
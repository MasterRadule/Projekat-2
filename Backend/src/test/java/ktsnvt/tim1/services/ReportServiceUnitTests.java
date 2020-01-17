package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.DailyReportDTO;
import ktsnvt.tim1.DTOs.ReportDTO;
import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReportServiceUnitTests {
    @Autowired
    private ReportService reportService;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private EventRepository eventRepository;

    private static DateTimeFormatter dateTimeFormatter;

    @BeforeAll
    public static void setUpDateTimeFormatter() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void getReport_endDateBeforeStartDate_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds, null,
                null);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_locationDoesNotExist_entityNotFoundExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds, locationId,
                null);

        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventDoesNotExist_entityNotFoundExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 2L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Location location = new Location(locationId, "Spens", 50.0, 50.0, false);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventIsCancelled_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 2L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Location location = new Location(locationId, "Spens", 50.0, 50.0, false);
        Event event = new Event();
        event.setId(eventId);
        event.setCancelled(true);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventIsActiveForReservations_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 2L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Location location = new Location(locationId, "Spens", 50.0, 50.0, false);
        Event event = new Event();
        event.setId(eventId);
        event.setCancelled(false);
        event.setActiveForReservations(true);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventIsNotFinishedYet_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 2L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Location location = new Location(locationId, "Spens", 50.0, 50.0, false);
        Event event = new Event();
        event.setId(eventId);
        event.setActiveForReservations(false);

        EventDay eventDay = new EventDay(1L, LocalDateTime.now().plusDays(1));
        eventDay.setEvent(event);
        event.getEventDays().add(eventDay);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_everythingIsOk_reportDTOReturned() throws BadParametersException, EntityNotFoundException {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        Long locationId = 1L;
        Long eventId = 2L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Location location = new Location(locationId, "Spens", 50.0, 50.0, false);
        Event event = new Event();
        event.setId(eventId);
        event.setActiveForReservations(false);

        EventDay eventDay = new EventDay(1L, LocalDateTime.now().minusDays(3));
        eventDay.setEvent(event);
        event.getEventDays().add(eventDay);

        DailyReportDTO dailyReportDTO = new DailyReportDTO(startDate, 1, 31);
        List<DailyReportDTO> repositoryResult = new ArrayList<>();
        repositoryResult.add(dailyReportDTO);

        int resultSize = 1;

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(reservationRepository.getAttendanceAndEarningsForPeriod(startDate, endDate, locationId, eventId))
                .thenReturn(repositoryResult);

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        ReportDTO result = reportService.getReport(reportRequestDTO);

        assertNotNull(result);
        assertEquals(resultSize, result.getLabels().size());

        for (int i = 0; i < resultSize; i++) {
            assertEquals(repositoryResult.get(i).getDate().format(formatter), result.getLabels().get(i));
            assertEquals(repositoryResult.get(i).getEarnings(), result.getEarnings().get(i));
            assertEquals(repositoryResult.get(i).getTicketCount(), result.getTickets().get(i));
        }

        verify(reservationRepository, times(1)).getAttendanceAndEarningsForPeriod(startDate, endDate, locationId,
                eventId);
    }


}

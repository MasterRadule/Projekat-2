package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.DailyReportDTO;
import ktsnvt.tim1.DTOs.ReportDTO;
import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReportServiceIntegrationTests {
    @Autowired
    private ReportService reportService;

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
        Long locationId = 31L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds, locationId,
                null);

        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventDoesNotExist_entityNotFoundExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 150L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventIsCancelled_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 3L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_eventIsActiveForReservations_badParametersExceptionThrown() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 1L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                locationId, eventId);

        assertThrows(BadParametersException.class, () -> reportService.getReport(reportRequestDTO));
    }

    @Test
    public void getReport_everythingOk_reportDTOReturned() throws BadParametersException, EntityNotFoundException {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 13:44:33", dateTimeFormatter);

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ReportRequestDTO reportRequestDTO = new ReportRequestDTO(startDateMilliseconds, endDateMilliseconds,
                null, null);

        int expectedNumberOfResults = 2;

        ReportDTO result = reportService.getReport(reportRequestDTO);

        assertNotNull(result);
        assertEquals(expectedNumberOfResults, result.getLabels().size());
    }

}

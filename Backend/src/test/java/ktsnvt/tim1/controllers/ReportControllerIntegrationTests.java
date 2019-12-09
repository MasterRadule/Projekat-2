package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.ReportDTO;
import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.utils.RestResponsePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
public class ReportControllerIntegrationTests {
    @Autowired
    TestRestTemplate testRestTemplate;

    private static DateTimeFormatter dateTimeFormatter;

    @BeforeAll
    public static void setUpDateTimeFormatter() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void getReport_endDateBeforeStartDate_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                "=%d", startDateMilliseconds, endDateMilliseconds), HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start date must be before end date", response.getBody());
    }

    @Test
    public void getReport_eventIdNotNullAndLocationIdIsNull_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long eventId = 1L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                        "=%d&eventId=%d", startDateMilliseconds, endDateMilliseconds, eventId), HttpMethod.GET, null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Location id must be provided if event id is specified", response.getBody());
    }

    @Test
    public void getReport_locationDoesNotExist_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 31L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                        "=%d&locationId=%d", startDateMilliseconds, endDateMilliseconds, locationId), HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Location not found", response.getBody());
    }

    @Test
    public void getReport_eventDoesNotExist_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 150L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                        "=%d&locationId=%d&eventId=%d", startDateMilliseconds, endDateMilliseconds, locationId, eventId),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Event not found", response.getBody());
    }

    @Test
    public void getReport_eventIsCancelled_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 3L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                        "=%d&locationId=%d&eventId=%d", startDateMilliseconds, endDateMilliseconds, locationId, eventId),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot generate report for cancelled event", response.getBody());
    }

    @Test
    public void getReport_eventIsActiveForReservations_errorMessageReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2019-11-08 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2019-11-11 13:44:33", dateTimeFormatter);
        Long locationId = 1L;
        Long eventId = 1L;

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d&endDate" +
                        "=%d&locationId=%d&eventId=%d", startDateMilliseconds, endDateMilliseconds, locationId, eventId),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot generate report for upcoming event", response.getBody());
    }

    @Test
    public void getReport_everythingOk_reportDTOReturned() throws BadParametersException, EntityNotFoundException {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01 13:44:33", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 13:44:33", dateTimeFormatter);

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ParameterizedTypeReference<List<ReportDTO>> responseType =
                new ParameterizedTypeReference<List<ReportDTO>>() {
                };

        int expectedNumberOfResults = 2;

        ResponseEntity<List<ReportDTO>> response = testRestTemplate
                .exchange(String.format("/reports?startDate=%d&endDate=%d", startDateMilliseconds, endDateMilliseconds),
                        HttpMethod.GET,
                        null, responseType
                );

        List<ReportDTO> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(expectedNumberOfResults, body.size());
    }

    @Test
    public void getReport_missingStartDate_badRequestStatusCodeReturned() {
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 13:44:33", dateTimeFormatter);

        Long endDateMilliseconds = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?endDate=%d",
                endDateMilliseconds),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getReport_missingEndDate_badRequestStatusCodeReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-05 13:44:33", dateTimeFormatter);

        Long startDateMilliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ResponseEntity<String> response = testRestTemplate.exchange(String.format("/reports?startDate=%d",
                startDateMilliseconds),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}

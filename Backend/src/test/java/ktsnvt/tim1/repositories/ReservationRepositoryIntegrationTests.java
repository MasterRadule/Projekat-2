package ktsnvt.tim1.repositories;

import ktsnvt.tim1.DTOs.DailyReportDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryIntegrationTests {
    @Autowired
    private ReservationRepository reservationRepository;

    private static DateTimeFormatter dateTimeFormatter;

    @BeforeAll
    public static void setUpDateFormatter() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void getAttendanceAndEarningsForPeriod_locationIdAndEventIdAreNull_valuesReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-03 00:00:00", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 00:00:00", dateTimeFormatter);

        int expectedNumberOfDays = 2;
        List<DailyReportDTO> expectedResult = new ArrayList<>();
        expectedResult.add(new DailyReportDTO(startDate, 1, 5));
        expectedResult.add(new DailyReportDTO(endDate, 1, 30));

        List<DailyReportDTO> returnedResults = reservationRepository.getAttendanceAndEarningsForPeriod(startDate, endDate,
                null, null);

        assertNotNull(returnedResults);
        assertEquals(expectedNumberOfDays, returnedResults.size());
        for (int i = 0; i < expectedNumberOfDays; i++) {
            assertEquals(expectedResult.get(i).getDate(), returnedResults.get(i).getDate());
            assertEquals(expectedResult.get(i).getTicketCount(), returnedResults.get(i).getTicketCount());
            assertEquals(expectedResult.get(i).getEarnings(), returnedResults.get(i).getEarnings());
        }
    }

    @Test
    public void getAttendanceAndEarningsForPeriod_locationIdIsNotNull_valuesReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01 00:00:00", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 00:00:00", dateTimeFormatter);

        LocalDateTime midDate = LocalDateTime.parse("2020-01-03 00:00:00", dateTimeFormatter);

        Long locationId = 2L;

        int expectedNumberOfDays = 1;
        List<DailyReportDTO> expectedResult = new ArrayList<>();
        expectedResult.add(new DailyReportDTO(midDate, 1, 5));

        List<DailyReportDTO> returnedResults = reservationRepository.getAttendanceAndEarningsForPeriod(startDate, endDate,
                locationId, null);

        assertNotNull(returnedResults);
        assertEquals(expectedNumberOfDays, returnedResults.size());
        for (int i = 0; i < expectedNumberOfDays; i++) {
            assertEquals(expectedResult.get(i).getDate(), returnedResults.get(i).getDate());
            assertEquals(expectedResult.get(i).getTicketCount(), returnedResults.get(i).getTicketCount());
            assertEquals(expectedResult.get(i).getEarnings(), returnedResults.get(i).getEarnings());
        }
    }

    @Test
    public void getAttendanceAndEarningsForPeriod_bothIdsAreProvided_valuesReturned() {
        LocalDateTime startDate = LocalDateTime.parse("2020-01-01 00:00:00", dateTimeFormatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-01-05 00:00:00", dateTimeFormatter);

        LocalDateTime midDate = LocalDateTime.parse("2020-01-03 00:00:00", dateTimeFormatter);

        Long locationId = 2L;
        Long eventId = 2L;

        int expectedNumberOfDays = 1;
        List<DailyReportDTO> expectedResult = new ArrayList<>();
        expectedResult.add(new DailyReportDTO(midDate, 1, 5));

        List<DailyReportDTO> returnedResults = reservationRepository.getAttendanceAndEarningsForPeriod(startDate, endDate,
                locationId, eventId);

        assertNotNull(returnedResults);
        assertEquals(expectedNumberOfDays, returnedResults.size());
        for (int i = 0; i < expectedNumberOfDays; i++) {
            assertEquals(expectedResult.get(i).getDate(), returnedResults.get(i).getDate());
            assertEquals(expectedResult.get(i).getTicketCount(), returnedResults.get(i).getTicketCount());
            assertEquals(expectedResult.get(i).getEarnings(), returnedResults.get(i).getEarnings());
        }
    }
}

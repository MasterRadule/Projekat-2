package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Seat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class SeatRepositoryIntegrationTest {

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void findByEventAndById_noSuchSeat_returnedNullOptional() {
        Long eventId = 1l;
        Long seatId = 10l;

        Optional<Seat> seatOptional = seatRepository.findByEventAndById(eventId, seatId);
        assertFalse(seatOptional.isPresent());
    }

    @Test
    void findByEventAndById_suchSeatExists_returnedSuchEvent() {
        Long eventId = 1L;
        Long seatId = 1L;

        Optional<Seat> seatOptional = seatRepository.findByEventAndById(eventId, seatId);
        assertTrue(seatOptional.isPresent());
        Seat seat = seatOptional.get();
        assertEquals(seatId, seat.getId());
        assertEquals(1, seat.getRowNum());
        assertEquals(1, seat.getColNum());
        assertEquals(1, seat.getTicket().getId());
        assertEquals(1, seat.getReservableSeatGroup().getId());
    }


    @Test
    void getSeatsByRowNumAndColNum_noSuchSeats_returnedEmptyList() {
        Long eventId = 2L;
        Long eventSeatGroupId = 1L;
        Integer rowNum = 1;
        Integer colNum = 1;

        List<Seat> seats = seatRepository.getSeatsByRowNumAndColNum(eventId, eventSeatGroupId, rowNum, colNum);
        assertTrue(seats.isEmpty());
    }

    @Test
    void getSeatsByRowNumAndColNum_suchSeatsExist_returnedSuchSeats() {
        Long eventId = 1L;
        Long eventSeatGroupId = 1L;
        Integer rowNum = 1;
        Integer colNum = 1;

        List<Seat> seats = seatRepository.getSeatsByRowNumAndColNum(eventId, eventSeatGroupId, rowNum, colNum);
        assertFalse(seats.isEmpty());
        assertEquals(2, seats.size());

        Seat seat0 = seats.get(0);
        assertEquals(1, seat0.getId());
        assertEquals(1, seat0.getRowNum());
        assertEquals(1, seat0.getColNum());
        assertEquals(1, seat0.getTicket().getId());
        assertEquals(1, seat0.getReservableSeatGroup().getId());

        Seat seat1 = seats.get(1);
        assertEquals(47, seat1.getId());
        assertEquals(1, seat1.getRowNum());
        assertEquals(1, seat1.getColNum());
        assertNull(seat1.getTicket());
        assertEquals(51, seat1.getReservableSeatGroup().getId());
    }

}

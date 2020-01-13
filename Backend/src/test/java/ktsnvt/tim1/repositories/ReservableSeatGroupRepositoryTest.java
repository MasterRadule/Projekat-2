package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.ReservableSeatGroup;
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
public class ReservableSeatGroupRepositoryTest {

    @Autowired
    private ReservableSeatGroupRepository reservableSeatGroupRepository;

    @Test
    void findByEventAndById_noSuchReservableSeatGroup_returnedNullOptional() {
        Long eventId = 1L;
        Long reservableSeatGroupId = 10L;

        Optional<ReservableSeatGroup> seatOptional = reservableSeatGroupRepository.findByEventAndById(eventId, reservableSeatGroupId);
        assertFalse(seatOptional.isPresent());
    }

    @Test
    void findByEventAndById_suchReservableSeatGroupExists_returnedSuchReservableSeatGroup() {
        Long eventId = 1L;
        Long reservableSeatGroupId = 1L;

        Optional<ReservableSeatGroup> reservableSeatGroupOptional = reservableSeatGroupRepository.findByEventAndById(eventId, reservableSeatGroupId);
        assertTrue(reservableSeatGroupOptional.isPresent());
        ReservableSeatGroup reservableSeatGroup = reservableSeatGroupOptional.get();
        assertEquals(reservableSeatGroupId, reservableSeatGroup.getId());
        assertEquals(8, reservableSeatGroup.getFreeSeats());
        assertEquals(1, reservableSeatGroup.getEventDay().getId());
        assertEquals(1, reservableSeatGroup.getEventSeatGroup().getId());
    }

    @Test
    void findByEventAndByEventSeatGroup_noSuchReservableSeatGroups_returnedEmptyList() {
        Long eventId = 2L;
        Long eventSeatGroupId = 1L;

        List<ReservableSeatGroup> reservableSeatGroups = reservableSeatGroupRepository.findByEventAndByEventSeatGroup(eventId, eventSeatGroupId);
        assertTrue(reservableSeatGroups.isEmpty());
    }

    @Test
    void findByEventAndByEventSeatGroup_suchReservableSeatGroupsExist_returnedSuchReservableSeatGroups() {
        Long eventId = 1L;
        Long eventSeatGroupId = 1L;

        List<ReservableSeatGroup> reservableSeatGroups = reservableSeatGroupRepository.findByEventAndByEventSeatGroup(eventId, eventSeatGroupId);
        assertFalse(reservableSeatGroups.isEmpty());
        assertEquals(2, reservableSeatGroups.size());

        ReservableSeatGroup reservableSeatGroup0 = reservableSeatGroups.get(0);
        assertEquals(1, reservableSeatGroup0.getId());
        assertEquals(8, reservableSeatGroup0.getFreeSeats());
        assertEquals(1, reservableSeatGroup0.getEventDay().getId());
        assertEquals(1, reservableSeatGroup0.getEventSeatGroup().getId());

        ReservableSeatGroup reservableSeatGroup1 = reservableSeatGroups.get(1);
        assertEquals(51, reservableSeatGroup1.getId());
        assertEquals(1, reservableSeatGroup1.getFreeSeats());
        assertEquals(26, reservableSeatGroup1.getEventDay().getId());
        assertEquals(1, reservableSeatGroup1.getEventSeatGroup().getId());
    }
}

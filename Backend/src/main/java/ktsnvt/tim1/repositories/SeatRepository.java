package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "select s from Seat s join s.reservableSeatGroup rsg join rsg.eventSeatGroup  esg join esg.event e " +
                   "where e.id = :eventId and s.id = :seatId")
    Optional<Seat> findByEventAndById(@Param("eventId") Long eventId, @Param("seatId") Long seatId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "select s from Seat s join s.reservableSeatGroup rsg join rsg.eventSeatGroup  esg join esg.event e " +
                   "where e.id = :eventId and esg.id = :eventSeatGroupId and s.colNum = :colNum and s.rowNum = :rowNum")
    List<Seat> getSeatsByRowNumAndColNum(@Param("eventId") Long eventId, @Param("eventSeatGroupId") Long eventSeatGroupId,
                                         @Param("rowNum") Integer rowNum, @Param("colNum") Integer colNum);
}

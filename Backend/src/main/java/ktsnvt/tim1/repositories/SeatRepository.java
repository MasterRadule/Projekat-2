package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    //@Lock(LockModeType.OPTIMISTIC)
    Optional<Seat> findById(Long id);

    //@Lock(LockModeType.OPTIMISTIC)
    @Query(value = "select s from Seat s join s.reservableSeatGroup rsg join rsg.eventSeatGroup  esg " +
                   "where esg.id = :eventSeatGroupId and s.colNum = :colNum and s.rowNum = :rowNum")
    List<Seat> getSeatsByRowNumAndColNum(@Param("eventSeatGroupId") Long eventSeatGroupId, @Param("rowNum") Integer rowNum, @Param("colNum") Integer colNum);
}

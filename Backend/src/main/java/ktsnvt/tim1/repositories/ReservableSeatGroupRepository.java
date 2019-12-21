package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.ReservableSeatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ReservableSeatGroupRepository extends JpaRepository<ReservableSeatGroup, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select rsg from ReservableSeatGroup rsg join rsg.eventSeatGroup  esg join esg.event e " +
            "where e.id = :eventId and rsg.id = :reservableSeatGroupId")
    Optional<ReservableSeatGroup> findByEventAndById(@Param("eventId") Long eventId,
                                                     @Param("reservableSeatGroupId") Long reservableSeatGroupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select rsg from ReservableSeatGroup rsg join rsg.eventSeatGroup  esg join esg.event e " +
            "where e.id = :eventId and esg.id = :eventSeatGroupId")
    List<ReservableSeatGroup> findByEventAndByEventSeatGroup(@Param("eventId") Long eventId,
                                                             @Param("eventSeatGroupId") Long eventSeatGroupId);
}

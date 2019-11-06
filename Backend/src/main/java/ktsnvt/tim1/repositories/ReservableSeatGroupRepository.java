package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.ReservableSeatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ReservableSeatGroupRepository extends JpaRepository<ReservableSeatGroup, Long> {
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReservableSeatGroup> findById(Long id);

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ReservableSeatGroup> findByEventSeatGroup(Long eventSeatGroupId);
}

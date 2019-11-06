package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.EventSeatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventSeatGroupRepository extends JpaRepository<EventSeatGroup, Long> {
    Optional<EventSeatGroup> findByIdAndEvent(Long id, Long eventId);
}

package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.EventDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventDayRepository extends JpaRepository<EventDay, Long> {
    Optional<EventDay> findByIdAndEvent(Long id, Long eventId);
}

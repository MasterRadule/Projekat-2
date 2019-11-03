package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIsActiveForReservationsTrueAndIsCancelledFalseAndById(Long eventId);
}

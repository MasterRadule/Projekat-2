package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}

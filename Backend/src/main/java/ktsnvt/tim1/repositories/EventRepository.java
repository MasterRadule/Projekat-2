package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findOneByName(String name);

    @Query(value = "select * from event e where lower(e.name) like ?1 and e.category like ?2 and cast(location_id as char) like ?3", nativeQuery = true)
    Page<Event> searchEvents(String name, String category, String locationID, Pageable pageable);
}

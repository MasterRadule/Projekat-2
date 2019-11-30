package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndIsCancelledFalse(Long eventId);

    Optional<Event> findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(Long eventId);

    Event findOneByName(String name);

    Page<Event> findAll(Pageable pageable);

    @Query(value = "select e from Event e join e.location l where lower(e.name) like ?1 and e.category.name like ?2 and (?3 is null or l.id = ?3)")
    Page<Event> searchEvents(String name, String category, Long locationID, Pageable pageable);
}

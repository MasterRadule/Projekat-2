package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndIsCancelledFalseAndLocationNotNull(Long eventId);

    Optional<Event> findByIsActiveForReservationsTrueAndIsCancelledFalseAndId(Long eventId);

    Event findOneByName(String name);

    Page<Event> findAll(Pageable pageable);

    @Query(value = "select e from Event e join e.location l where lower(e.name) like :name and (:category is null or e.category = :category) and (:location_id is null or l.id = :location_id)")
    Page<Event> searchEvents(@Param("name") String name, @Nullable @Param("category") EventCategory category, @Nullable @Param("location_id") Long locationID, Pageable pageable);
}

package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByOrderIdIsNull(Pageable pageable);

    Page<Reservation> findByOrderIdIsNotNull(Pageable pageable);

    @Query(value = "select ed.date, count(t), sum(esg.price) from Reservation r join r.tickets t join t.eventDays ed " +
            "join t.eventSeatGroup esg join r.event e join e.location l where r.orderId is not null and ed.date " +
            "between ?1 and ?2 and (?3 is null or l.id = ?3) and (?4 is null or e.id = ?4) group by ed.date order by " +
            "ed.date asc")
    List<Object[]> getAttendanceAndEarningsForPeriod(Date startDate, Date endDate, Long locationId, Long eventId);
}
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

    @Query(nativeQuery = true, value = "SELECT DATE(ed.DATE), COUNT(*) FROM RESERVATION r INNER JOIN " +
            "EVENT_DAY_RESERVATIONS edr ON r.ID = edr.RESERVATION INNER JOIN EVENT_DAY ed ON edr.EVENT_DAY = ed.ID " +
            "WHERE r.IS_PAID = TRUE AND DATE(ed.date) BETWEEN :startDate AND :endDate GROUP BY DATE(ed.date) " +
            "ORDER BY (ed.date) asc;")
    List<Object[]> getEarningsForPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

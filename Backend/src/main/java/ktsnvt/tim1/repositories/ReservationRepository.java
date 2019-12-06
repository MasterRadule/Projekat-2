package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByOrderIdIsNullAndIsCancelledFalse(Pageable pageable);

    Page<Reservation> findByOrderIdIsNotNullAndIsCancelledFalse(Pageable pageable);

    List<Reservation> findByOrderIdIsNullAndIsCancelledFalse();

    Optional<Reservation> findByIdAndIsCancelledFalse(Long id);

    @Query(value = "select ed.date, count(t), sum(esg.price) from Reservation r join r.tickets t join t" +
            ".reservableSeatGroups rsg join rsg.eventSeatGroup esg join rsg.eventDay ed join r.event e join e" +
            ".location l where r.orderId is not null and ed.date between :startDate and :endDate and (:locationId is " +
            "null or l.id = :locationId) and (:eventId is null or e.id = :eventId) group by ed.date order by ed.date " +
            "asc")
    List<Object[]> getAttendanceAndEarningsForPeriod(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     @Param("locationId") @Nullable Long locationId,
                                                     @Param("eventId") @Nullable Long eventId);
}

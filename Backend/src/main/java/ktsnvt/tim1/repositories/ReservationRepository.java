package ktsnvt.tim1.repositories;

import ktsnvt.tim1.DTOs.DailyReportDTO;
import ktsnvt.tim1.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByRegisteredUserIdAndOrderIdIsNullAndIsCancelledFalse(Long registeredUserId, Pageable pageable);

    Page<Reservation> findByRegisteredUserIdAndOrderIdIsNotNullAndIsCancelledFalse(Long registeredUserId, Pageable pageable);

    Page<Reservation> findByRegisteredUserIdAndIsCancelledFalse(Long registeredUserId, Pageable pageable);

    List<Reservation> findByOrderIdIsNullAndIsCancelledFalse();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByIdAndRegisteredUserIdAndIsCancelledFalse(Long id, Long registeredUserId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Reservation> findByIdAndIsCancelledFalseAndRegisteredUserId(Long id, Long registeredUserId);

    @Query(value = "select new ktsnvt.tim1.DTOs.DailyReportDTO(ed.date, count(t), sum(esg.price)) from Reservation r join " +
            "r.tickets t join t" +
            ".reservableSeatGroups rsg join rsg.eventSeatGroup esg join rsg.eventDay ed join r.event e join e" +
            ".location l where (r.orderId is not null) and (ed.date between :startDate and :endDate) and (:locationId" +
            " is " +
            "null or l.id = :locationId) and (:eventId is null or e.id = :eventId) group by ed.date order by ed.date " +
            "asc")
    List<DailyReportDTO> getAttendanceAndEarningsForPeriod(@Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate,
                                                           @Param("locationId") @Nullable Long locationId,
                                                           @Param("eventId") @Nullable Long eventId);
}

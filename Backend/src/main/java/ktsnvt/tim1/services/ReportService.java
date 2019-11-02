package ktsnvt.tim1.services;

import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Object[]> getReport(Date startDate, Date endDate, Long locationId, Long eventId) {
        return reservationRepository.getEarningsForPeriod(startDate, endDate);
    }

}

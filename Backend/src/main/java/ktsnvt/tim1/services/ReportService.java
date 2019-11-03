package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Object[]> getReport(ReportRequestDTO reportRequest) throws BadParametersException {
        if (reportRequest.getEndDate().compareTo(reportRequest.getStartDate()) <= 0)
            throw new BadParametersException("Start date must be before end date");

        return reservationRepository.getAttendanceAndEarningsForPeriod(reportRequest.getStartDate(),
                reportRequest.getEndDate(),
                reportRequest.getLocationId(), reportRequest.getEventId());
    }

}

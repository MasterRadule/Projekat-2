package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.DailyReportDTO;
import ktsnvt.tim1.DTOs.ReportDTO;
import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EventRepository eventRepository;

    public ReportDTO getReport(ReportRequestDTO reportRequest) throws BadParametersException, EntityNotFoundException {
        LocalDateTime startDate =
                Instant.ofEpochMilli(reportRequest.getStartDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDate =
                Instant.ofEpochMilli(reportRequest.getEndDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (endDate.compareTo(startDate) <= 0)
            throw new BadParametersException("Start date must be before end date");

        if (reportRequest.getLocationId() != null) {
            Optional<Location> location = locationRepository.findById(reportRequest.getLocationId());

            if (!location.isPresent())
                throw new EntityNotFoundException("Location not found");
        }
        if (reportRequest.getEventId() != null) {
            Optional<Event> event = eventRepository.findById(reportRequest.getEventId());

            if (!event.isPresent())
                throw new EntityNotFoundException("Event not found");

            Event e = event.get();

            if (e.getCancelled())
                throw new BadParametersException("Cannot generate report for cancelled event");
            else if (e.getActiveForReservations())
                throw new BadParametersException("Cannot generate report for upcoming event");
            else if (e.getEventDays().stream().anyMatch(ed -> ed.getDate().compareTo(LocalDateTime.now()) >= 0))
                throw new BadParametersException("Cannot generate report for event that is not finished");
        }
        return new ReportDTO(reservationRepository.getAttendanceAndEarningsForPeriod(startDate,
                endDate, reportRequest.getLocationId(), reportRequest.getEventId()));
    }
}

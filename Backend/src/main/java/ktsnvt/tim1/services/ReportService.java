package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ReportRequestDTO;
import ktsnvt.tim1.exceptions.BadParametersException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public List<Object[]> getReport(ReportRequestDTO reportRequest) throws BadParametersException, EntityNotFoundException {
        if (reportRequest.getEndDate().compareTo(reportRequest.getStartDate()) <= 0)
            throw new BadParametersException("Start date must be before end date");
        else if (reportRequest.getLocationId() == null && reportRequest.getEventId() != null)
            throw new BadParametersException("Location id must be provided if event id is specified");

        if (reportRequest.getLocationId() != null) {
            Optional<Location> location = locationRepository.findById(reportRequest.getLocationId());

            if (!location.isPresent())
                throw new EntityNotFoundException("Location not found");

            Location l = location.get();

            if (l.isDisabled())
                throw new BadParametersException("Cannot generate report for disabled location");
        } else if (reportRequest.getEventId() != null) {
            Optional<Event> event = eventRepository.findById(reportRequest.getEventId());

            if (!event.isPresent())
                throw new EntityNotFoundException("Event not found");

            Event e = event.get();

            if (e.getCancelled())
                throw new BadParametersException("Cannot generate report for cancelled event");
            else if (e.getActiveForReservations())
                throw new BadParametersException("Cannot generate report for upcoming event");
            else if (e.getEventDays().stream().anyMatch(ed -> ed.getDate().compareTo(new Date()) >= 0))
                throw new BadParametersException("Cannot generate report for event that is not finished");
        }
        return reservationRepository.getAttendanceAndEarningsForPeriod(reportRequest.getStartDate(),
                reportRequest.getEndDate(),
                reportRequest.getLocationId(), reportRequest.getEventId());
    }
}

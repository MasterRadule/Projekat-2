package ktsnvt.tim1.DTOs;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ReportRequestDTO {
    @NotNull
    @DateTimeFormat(pattern = "dd.MM.yyyy.")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "dd.MM.yyyy.")
    private Date endDate;

    private Long locationId;
    private Long eventId;

    public ReportRequestDTO() {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}

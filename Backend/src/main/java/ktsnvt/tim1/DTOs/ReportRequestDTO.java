package ktsnvt.tim1.DTOs;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ReportRequestDTO {
    @NotNull
    private Long startDate;

    @NotNull
    private Long endDate;

    private Long locationId;
    private Long eventId;

    public ReportRequestDTO() {
    }

    public ReportRequestDTO(@NotNull Long startDate, @NotNull Long endDate, Long locationId, Long eventId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationId = locationId;
        this.eventId = eventId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
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

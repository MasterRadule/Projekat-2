package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.EventCategory;

import javax.validation.constraints.NotNull;

public class SearchEventsDTO {

    @NotNull(message = "Name must be specified or empty string")
    private String name;

    private Long locationID;

    private EventCategory category;

    private String fromDate;
    private String toDate;

    public SearchEventsDTO() {}

    public SearchEventsDTO(String name, Long locationID, EventCategory category, String fromDate, String toDate) {
        this.name = name;
        this.locationID = locationID;
        this.category = category;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLocationID() {
        return locationID;
    }

    public void setLocationID(Long locationID) {
        this.locationID = locationID;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}

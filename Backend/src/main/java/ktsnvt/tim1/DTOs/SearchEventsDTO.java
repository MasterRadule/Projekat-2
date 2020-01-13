package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.EventCategory;

import javax.validation.constraints.NotNull;

public class SearchEventsDTO {

    @NotNull(message = "Name must be specified or empty string")
    private String name;

    private Long locationID;

    private EventCategory category;

    private String startDate;
    private String endDate;

    public SearchEventsDTO() {}

    public SearchEventsDTO(String name, Long locationID, EventCategory category, String startDate, String endDate) {
        this.name = name;
        this.locationID = locationID;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}

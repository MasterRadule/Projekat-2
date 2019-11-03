package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotNull;

public class SearchEventsDTO {

    @NotNull(message = "Name must be specified or empty string")
    private String name;

    private Long locationID;

    @NotNull(message = "Category must be specified or empty string")
    private String category;

    private String fromDate;
    private String toDate;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

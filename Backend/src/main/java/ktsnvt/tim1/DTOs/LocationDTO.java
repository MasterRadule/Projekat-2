package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LocationDTO {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Latitude must be specified")
    private Double latitude;

    @NotNull(message = "Longitude must be specified")
    private Double longitude;

    @NotNull(message = "Location status must be specified")
    private Boolean disabled;

    public LocationDTO() {
    }

    public LocationDTO(Location location) {
        this.id = location.getId();
        this.name = location.getName();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.disabled = location.getDisabled();
    }

    public Location convertToEntity() {
        Location l = new Location();
        l.setDisabled(this.disabled);
        l.setId(this.id);
        l.setLatitude(this.latitude);
        l.setLongitude(this.longitude);
        l.setName(this.name);

        return l;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}

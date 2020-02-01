package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.Location;

public class LocationOptionDTO {
    private Long id;
    private String name;

    public LocationOptionDTO() {
    }

    public LocationOptionDTO(Location l) {
        this.id = l.getId();
        this.name = l.getName();
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
}

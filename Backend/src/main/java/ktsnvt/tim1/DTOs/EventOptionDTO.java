package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.Event;

public class EventOptionDTO {
    private Long id;
    private String name;

    public EventOptionDTO(Event e) {
        this.id = e.getId();
        this.name = e.getName();
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

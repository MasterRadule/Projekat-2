package ktsnvt.tim1.DTOs;


import javax.validation.constraints.NotBlank;
public class EventDayDTO {

    private Long id;

    @NotBlank(message = "Event day's date must be specified")
    private String date;

    public EventDayDTO() {
    }

    public EventDayDTO(Long id, String date) {
        this.id = id;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

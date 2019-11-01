package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.EventDay;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EventDayDTO {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy. HH:mm");

    private Long id;

    @NotNull(message = "Event day's date must be specified")
    private String date;

    public EventDayDTO() {
    }

    public EventDayDTO(EventDay eventDay) {
        this.id = eventDay.getId();
        this.date = formatter.format(eventDay.getDate());
    }

    public EventDay convertToEntity() throws ParseException {
        EventDay ed = new EventDay();
        ed.setId(this.id);
        ed.setDate(formatter.parse(this.date));

        return ed;
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

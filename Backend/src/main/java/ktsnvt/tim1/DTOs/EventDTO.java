package ktsnvt.tim1.DTOs;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;

public class EventDTO {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Category must be specified")
    private String category;

    @NotNull(message = "Must be specified if event is active for reservations")
    private Boolean activeForReservations;

    @NotNull
    private Boolean cancelled;

    @Positive
    private Integer reservationDeadlineDays;

    @Positive
    private Integer maxTicketsPerReservation;

    @Valid
    @NotEmpty(message = "Event days must be specified")
    private ArrayList<EventDayDTO> eventDays;

    public EventDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean isActiveForReservations() {
        return activeForReservations;
    }

    public void setActiveForReservations(Boolean activeForReservations) {
        this.activeForReservations = activeForReservations;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Integer getReservationDeadlineDays() {
        return reservationDeadlineDays;
    }

    public void setReservationDeadlineDays(Integer reservationDeadlineDays) {
        this.reservationDeadlineDays = reservationDeadlineDays;
    }

    public Integer getMaxTicketsPerReservation() {
        return maxTicketsPerReservation;
    }

    public void setMaxTicketsPerReservation(Integer maxTicketsPerReservation) {
        this.maxTicketsPerReservation = maxTicketsPerReservation;
    }

    public ArrayList<EventDayDTO> getEventDays() {
        return eventDays;
    }

    public void setEventDays(ArrayList<EventDayDTO> eventDays) {
        this.eventDays = eventDays;
    }
}

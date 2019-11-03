package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.model.EventDay;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Integer maxReservationsPerUser;

    @Valid
    @NotNull(message = "Event days must be specified")
    @NotEmpty(message = "Event days must be specified")
    private ArrayList<EventDayDTO> eventDays;

    public EventDTO() {
    }

    public EventDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.category = event.getCategory().name();
        this.activeForReservations = event.getActiveForReservations();
        this.reservationDeadlineDays = event.getReservationDeadlineDays();
        this.cancelled = event.getCancelled();
        this.maxReservationsPerUser = event.getMaxReservationsPerUser();
        this.eventDays = event.getEventDays().stream().map(EventDayDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Event convertToEntity() throws EntityNotValidException {
        Event e = new Event();
        e.setId(this.id);
        e.setName(this.name);
        e.setDescription(this.description);
        e.setCategory(EventCategory.valueOf(this.category));
        e.setActiveForReservations(this.activeForReservations);
        e.setMaxReservationsPerUser(this.maxReservationsPerUser);
        e.setReservationDeadlineDays(this.reservationDeadlineDays);
        e.setCancelled(this.cancelled);
        Set<EventDay> eventDays = new HashSet<>();
        for (EventDayDTO d : this.eventDays) {
            EventDay eventDay = d.convertToEntity();
            eventDay.setEvent(e);
            eventDays.add(eventDay);
        }
        e.setEventDays(eventDays);
        return e;
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

    public Integer getMaxReservationsPerUser() {
        return maxReservationsPerUser;
    }

    public void setMaxReservationsPerUser(Integer maxReservationsPerUser) {
        this.maxReservationsPerUser = maxReservationsPerUser;
    }

    public ArrayList<EventDayDTO> getEventDays() {
        return eventDays;
    }

    public void setEventDays(ArrayList<EventDayDTO> eventDays) {
        this.eventDays = eventDays;
    }
}

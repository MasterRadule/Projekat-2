package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventCategory category;

    private List<Object> pictures;

    private List<Object> videos;

    @Column(nullable = false)
    private boolean isActiveForReservations;

    @Column(nullable = false)
    private boolean isCancelled;

    @Column(nullable = false)
    private int reservationDeadlineDays;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<EventDay> eventDays;

    @ManyToOne()
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public Event() {
    }

    public Event(String name, String description, EventCategory category, List<Object> pictures, List<Object> videos, boolean isActiveForReservations, boolean isCancelled, int reservationDeadlineDays, Set<EventDay> eventDays, Location location) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.pictures = pictures;
        this.videos = videos;
        this.isActiveForReservations = isActiveForReservations;
        this.isCancelled = isCancelled;
        this.reservationDeadlineDays = reservationDeadlineDays;
        this.eventDays = eventDays;
        this.location = location;
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

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public List<Object> getPictures() {
        return pictures;
    }

    public void setPictures(List<Object> pictures) {
        this.pictures = pictures;
    }

    public List<Object> getVideos() {
        return videos;
    }

    public void setVideos(List<Object> videos) {
        this.videos = videos;
    }

    public boolean isActiveForReservations() {
        return isActiveForReservations;
    }

    public void setActiveForReservations(boolean activeForReservations) {
        isActiveForReservations = activeForReservations;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public int getReservationDeadlineDays() {
        return reservationDeadlineDays;
    }

    public void setReservationDeadlineDays(int reservationDeadlineDays) {
        this.reservationDeadlineDays = reservationDeadlineDays;
    }

    public Set<EventDay> getEventDays() {
        return eventDays;
    }

    public void setEventDays(Set<EventDay> eventDays) {
        this.eventDays = eventDays;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

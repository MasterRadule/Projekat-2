package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
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
    private Boolean isActiveForReservations;

    @Column(nullable = false)
    private Boolean isCancelled;

    @Column(nullable = false)
    private Integer reservationDeadlineDays;

    @Column(nullable = false)
    private Integer maxReservationsPerUser;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventDay> eventDays;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventSeatGroup> eventSeatGroups;

    @ManyToOne()
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public Event() {
        this.eventDays = new HashSet<>();
        this.eventSeatGroups = new HashSet<>();
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

    public Boolean getActiveForReservations() {
        return isActiveForReservations;
    }

    public void setActiveForReservations(Boolean activeForReservations) {
        isActiveForReservations = activeForReservations;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
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

    public Set<EventDay> getEventDays() {
        return eventDays;
    }

    public void setEventDays(Set<EventDay> eventDays) {
        this.eventDays = eventDays;
    }

    public Set<EventSeatGroup> getEventSeatGroups() {
        return eventSeatGroups;
    }

    public void setEventSeatGroups(Set<EventSeatGroup> eventSeatGroups) {
        this.eventSeatGroups = eventSeatGroups;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

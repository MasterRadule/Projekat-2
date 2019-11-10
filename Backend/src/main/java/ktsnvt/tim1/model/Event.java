package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MediaFile> picturesAndVideos;

    @Column(nullable = false)
    private Boolean isActiveForReservations = true;

    @Column(nullable = false)
    private Boolean isCancelled = false;

    @Column(nullable = false)
    private Integer reservationDeadlineDays;

    @Column(nullable = false)
    private Integer maxTicketsPerReservation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventDay> eventDays;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventSeatGroup> eventSeatGroups;

    @ManyToOne()
    @JoinColumn(name = "location_id")
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

    public Set<MediaFile> getPicturesAndVideos() {
        return picturesAndVideos;
    }

    public void setPicturesAndVideos(Set<MediaFile> picturesAndVideos) {
        this.picturesAndVideos = picturesAndVideos;
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

    public Integer getMaxTicketsPerReservation() {
        return maxTicketsPerReservation;
    }

    public void setMaxTicketsPerReservation(Integer maxTicketsPerReservation) {
        this.maxTicketsPerReservation = maxTicketsPerReservation;
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

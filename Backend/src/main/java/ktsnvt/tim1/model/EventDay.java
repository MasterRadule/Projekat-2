package ktsnvt.tim1.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Entity
public class EventDay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne()
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "eventDay")
    private Set<ReservableSeatGroup> reservableSeatGroups;

    public EventDay() {
        this.reservableSeatGroups = new HashSet<>();
    }

    public EventDay(Long id, LocalDateTime date) {
        this.id = id;
        this.date = date;
        this.reservableSeatGroups = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDay eventDay = (EventDay) o;
        int sameDate = date.compareTo(eventDay.getDate());

        return sameDate == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<ReservableSeatGroup> getReservableSeatGroups() {
        return reservableSeatGroups;
    }

    public void setReservableSeatGroups(Set<ReservableSeatGroup> reservableSeatGroups) {
        this.reservableSeatGroups = reservableSeatGroups;
    }
}

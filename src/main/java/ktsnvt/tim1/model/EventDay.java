package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
public class EventDay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "event_day_reservations", joinColumns = @JoinColumn(name = "event_day", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "reservation", referencedColumnName = "id"))
    private Set<Reservation> reservations;

    public EventDay() {
        this.reservations = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }
}

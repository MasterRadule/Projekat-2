package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class EventSeatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double price;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservableSeatGroup> reservableSeatGroups;

    @ManyToOne()
    @JoinColumn(name = "seat_group_id", nullable = false)
    private SeatGroup seatGroup;

    @ManyToOne()
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public EventSeatGroup() {
        this.reservableSeatGroups = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<ReservableSeatGroup> getReservableSeatGroups() {
        return reservableSeatGroups;
    }

    public void setReservableSeatGroups(Set<ReservableSeatGroup> reservableSeatGroups) {
        this.reservableSeatGroups = reservableSeatGroups;
    }

    public SeatGroup getSeatGroup() {
        return seatGroup;
    }

    public void setSeatGroup(SeatGroup seatGroup) {
        this.seatGroup = seatGroup;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

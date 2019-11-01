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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Reservation> reservations;

    @ManyToOne()
    @JoinColumn(name = "seat_group_id", nullable = false)
    private SeatGroup seatGroupB;

    @Column(nullable = false)
    private Integer freeSeats;

    public EventSeatGroup() {
        this.reservations = new HashSet<>();
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

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public SeatGroup getSeatGroupB() {
        return seatGroupB;
    }

    public void setSeatGroupB(SeatGroup seatGroupB) {
        this.seatGroupB = seatGroupB;
    }


}

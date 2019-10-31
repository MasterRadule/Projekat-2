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
    private Set<Reservation> reservation;

    @ManyToOne()
    @JoinColumn(name = "seat_group_id", nullable = false)
    private SeatGroup seatGroupB;

    public EventSeatGroup() {
        this.reservation = new HashSet<>();
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

    public Set<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(Set<Reservation> reservation) {
        this.reservation = reservation;
    }

    public SeatGroup getSeatGroupB() {
        return seatGroupB;
    }

    public void setSeatGroupB(SeatGroup seatGroupB) {
        this.seatGroupB = seatGroupB;
    }
}

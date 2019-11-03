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
    private Set<Ticket> tickets;

    @ManyToOne()
    @JoinColumn(name = "seat_group_id", nullable = false)
    private SeatGroup seatGroup;

    @Column(nullable = false)
    private Integer freeSeats;

    public EventSeatGroup() {
        this.tickets = new HashSet<>();
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

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public SeatGroup getSeatGroup() {
        return seatGroup;
    }

    public void setSeatGroup(SeatGroup seatGroup) {
        this.seatGroup = seatGroup;
    }

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }
}

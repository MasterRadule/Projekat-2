package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ReservableSeatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "reservable_seat_groups_tickets",
            joinColumns = @JoinColumn(name = "reservable_seat_group", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ticket", referencedColumnName = "id"))
    private Set<Ticket> tickets;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "event_day_id", nullable = false)
    private EventDay eventDay;

    @ManyToOne
    @JoinColumn(name = "event_seat_group_id", nullable = false)
    private EventSeatGroup eventSeatGroup;

    @Column(nullable = false)
    private Integer freeSeats;

    public ReservableSeatGroup() {
        this.tickets = new HashSet<>();
        this.seats = new HashSet<>();
    }

    public ReservableSeatGroup(EventDay eventDay, EventSeatGroup esg) {
        this();
        this.setEventDay(eventDay);
        this.setEventSeatGroup(esg);
        this.setFreeSeats(esg.getSeatGroup().getTotalSeats());
        if (!esg.getSeatGroup().getParterre()) {
            int rowsNum = esg.getSeatGroup().getRowsNum();
            int colsNum = esg.getSeatGroup().getColsNum();
            for (int i = 0; i < rowsNum; i++) {
                for (int j = 0; j < colsNum; j++) {
                    Seat s = new Seat(rowsNum + 1, colsNum + 1, this);
                    this.getSeats().add(s);
                }
            }
        }
    }

    public void incrementFreeSeats(){
        this.freeSeats++;
    }

    public boolean decrementFreeSeats(){
        if(this.freeSeats>0){
            this.freeSeats--;
            return true;
        }
        else return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

    public EventDay getEventDay() {
        return eventDay;
    }

    public void setEventDay(EventDay eventDay) {
        this.eventDay = eventDay;
    }

    public EventSeatGroup getEventSeatGroup() {
        return eventSeatGroup;
    }

    public void setEventSeatGroup(EventSeatGroup eventSeatGroup) {
        this.eventSeatGroup = eventSeatGroup;
    }

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }
}

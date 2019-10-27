package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private int rowNum;

    @Column(nullable = false)
    private int colNum;

    @Column(nullable = false)
    private boolean isPaid;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<EventDay> eventDays;

    @ManyToOne()
    @JoinColumn(name = "seat_group_id", nullable = false)
    private SeatGroup seatGroup;

    public Reservation() {
        this.eventDays = new HashSet<>();
    }

    public Reservation(int rowNum, int colNum, boolean isPaid, Set<EventDay> eventDays, SeatGroup seatGroup) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.isPaid = isPaid;
        this.eventDays = eventDays;
        this.seatGroup = seatGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Set<EventDay> getEventDays() {
        return eventDays;
    }

    public void setEventDays(Set<EventDay> eventDays) {
        this.eventDays = eventDays;
    }

    public SeatGroup getSeatGroup() {
        return seatGroup;
    }

    public void setSeatGroup(SeatGroup seatGroup) {
        this.seatGroup = seatGroup;
    }
}

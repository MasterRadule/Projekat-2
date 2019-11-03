package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Integer rowNum;

    @Column(nullable = false)
    private Integer colNum;

    @ManyToMany(mappedBy = "tickets")
    private Set<EventDay> eventDays;

    @ManyToOne()
    @JoinColumn(name = "event_seat_group_id", nullable = false)
    private EventSeatGroup eventSeatGroup;

    public Ticket() {
        this.eventDays = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }

    public Set<EventDay> getEventDays() {
        return eventDays;
    }

    public void setEventDays(Set<EventDay> eventDays) {
        this.eventDays = eventDays;
    }

    public EventSeatGroup getEventSeatGroup() {
        return eventSeatGroup;
    }

    public void setEventSeatGroup(EventSeatGroup eventSeatGroup) {
        this.eventSeatGroup = eventSeatGroup;
    }
}

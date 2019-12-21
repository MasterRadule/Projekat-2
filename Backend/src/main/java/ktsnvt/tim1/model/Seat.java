package ktsnvt.tim1.model;

import javax.persistence.*;

@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "reservable_seat_group_id", nullable = false)
    private ReservableSeatGroup reservableSeatGroup;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(nullable = false)
    private Integer rowNum;

    @Column(nullable = false)
    private Integer colNum;

    public Seat() {
    }

    public Seat(int rowsNum, int colsNum, ReservableSeatGroup reservableSeatGroup) {
        this.setRowNum(rowsNum);
        this.setColNum(colsNum);
        this.setReservableSeatGroup(reservableSeatGroup);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservableSeatGroup getReservableSeatGroup() {
        return reservableSeatGroup;
    }

    public void setReservableSeatGroup(ReservableSeatGroup reservableSeatGroup) {
        this.reservableSeatGroup = reservableSeatGroup;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
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
}

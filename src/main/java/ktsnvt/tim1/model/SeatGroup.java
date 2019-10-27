package ktsnvt.tim1.model;

import javax.persistence.*;

@Entity
public class SeatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private int rowsNum;

    @Column(nullable = false)
    private int colsNum;

    @Column(nullable = false)
    private int freeSeats;

    @Column(nullable = false)
    private boolean isParterre;

    @Column(nullable = false)
    private double price;

    public SeatGroup() {
    }

    public SeatGroup(int rowsNum, int colsNum, int freeSeats, boolean isParterre, double price) {
        this.rowsNum = rowsNum;
        this.colsNum = colsNum;
        this.freeSeats = freeSeats;
        this.isParterre = isParterre;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowsNum() {
        return rowsNum;
    }

    public void setRowsNum(int rowsNum) {
        this.rowsNum = rowsNum;
    }

    public int getColsNum() {
        return colsNum;
    }

    public void setColsNum(int colsNum) {
        this.colsNum = colsNum;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public boolean isParterre() {
        return isParterre;
    }

    public void setParterre(boolean parterre) {
        isParterre = parterre;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

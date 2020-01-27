package ktsnvt.tim1.model;

import javax.persistence.*;

@Entity
public class SeatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column()
    private Integer rowsNum;

    @Column()
    private Integer colsNum;

    @Column(nullable = false)
    private Boolean isParterre;

    @Column(nullable = false)
    private Double xCoordinate;

    @Column(nullable = false)
    private Double yCoordinate;

    @Column(nullable = false)
    private Double angle;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer totalSeats;

    @ManyToOne()
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public SeatGroup() {
    }

    public SeatGroup(Integer rowsNum, Integer colsNum, Boolean isParterre, Double xCoordinate, Double yCoordinate, Double angle, String name, Integer totalSeats, Location location) {
        this.rowsNum = rowsNum;
        this.colsNum = colsNum;
        this.isParterre = isParterre;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.angle = angle;
        this.name = name;
        this.totalSeats = totalSeats;
        this.location = location;
    }

    public SeatGroup(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRowsNum() {
        return rowsNum;
    }

    public void setRowsNum(Integer rowsNum) {
        this.rowsNum = rowsNum;
    }

    public Integer getColsNum() {
        return colsNum;
    }

    public void setColsNum(Integer colsNum) {
        this.colsNum = colsNum;
    }

    public Boolean getParterre() {
        return isParterre;
    }

    public void setParterre(Boolean parterre) {
        isParterre = parterre;
    }

    public Double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }
}

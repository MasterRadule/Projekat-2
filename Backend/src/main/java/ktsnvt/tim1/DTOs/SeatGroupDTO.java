package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotNull;

public class SeatGroupDTO {
    private Long id;

    private Integer rowsNum;

    private Integer colsNum;

    @NotNull(message = "Seat group parterre must be specified")
    private Boolean parterre;

    @NotNull(message = "X coordiante of seat group must be specified")
    private Double xCoordinate;

    @NotNull(message = "Y coordinate of seat group must be specified")
    private Double yCoordinate;

    private Integer totalSeats;

    @NotNull(message = "Seat group name must be provided")
    private String name;

    public SeatGroupDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean isParterre() {
        return parterre;
    }

    public void setParterre(Boolean parterre) {
        this.parterre = parterre;
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

    public Boolean getParterre() {
        return parterre;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}

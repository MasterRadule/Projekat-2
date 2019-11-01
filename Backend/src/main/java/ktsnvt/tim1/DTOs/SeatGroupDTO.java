package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.SeatGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SeatGroupDTO {
    private Long id;

    @NotNull(message = "Number of rows must be specified")
    @Min(value = 1, message = "Number of rows must be greater than zero")
    private Integer rowsNum;

    @NotNull(message = "Number of columns must be specified")
    @Min(value = 1, message = "Number of columns must be greater than zero")
    private Integer colsNum;

    @NotNull(message = "Number of free seats must be specified")
    @Min(value = 0, message = "Number of free seats cannot be negative")
    private Integer freeSeats;

    @NotNull(message = "Seat group parterre must be specified")
    private Boolean parterre;

    @NotNull(message = "X coordiante of seat group must be specified")
    private Double xCoordinate;

    @NotNull(message = "Y coordinate of seat group must be specified")
    private Double yCoordinate;

    public SeatGroupDTO() {
    }

    public SeatGroupDTO(SeatGroup seatGroup) {
        this.id = seatGroup.getId();
        this.colsNum = seatGroup.getColsNum();
        this.rowsNum = seatGroup.getRowsNum();
        this.freeSeats = seatGroup.getFreeSeats();
        this.parterre = seatGroup.getParterre();
        this.xCoordinate = seatGroup.getxCoordinate();
        this.yCoordinate = seatGroup.getyCoordinate();
    }

    public SeatGroup convertToEntity() {
        SeatGroup seatGroup = new SeatGroup();

        seatGroup.setColsNum(this.colsNum);
        seatGroup.setFreeSeats(this.freeSeats);
        seatGroup.setId(this.id);
        seatGroup.setParterre(this.parterre);
        seatGroup.setRowsNum(this.rowsNum);
        seatGroup.setxCoordinate(this.xCoordinate);
        seatGroup.setyCoordinate(this.yCoordinate);

        return seatGroup;
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

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
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
}

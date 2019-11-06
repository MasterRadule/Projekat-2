package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.SeatGroup;

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

    public SeatGroupDTO() {
    }

    public SeatGroupDTO(SeatGroup seatGroup) {
        this.id = seatGroup.getId();
        this.colsNum = seatGroup.getColsNum();
        this.rowsNum = seatGroup.getRowsNum();
        this.parterre = seatGroup.getParterre();
        this.xCoordinate = seatGroup.getxCoordinate();
        this.yCoordinate = seatGroup.getyCoordinate();
        this.totalSeats = seatGroup.getTotalSeats();
    }

    public SeatGroup convertToEntity() throws EntityNotValidException {
        SeatGroup seatGroup = new SeatGroup();

        seatGroup.setId(null);
        seatGroup.setParterre(this.parterre);
        seatGroup.setxCoordinate(this.xCoordinate);
        seatGroup.setyCoordinate(this.yCoordinate);

        if (this.isParterre()) {
            seatGroup.setColsNum(null);
            seatGroup.setRowsNum(null);
            if (this.totalSeats == null || this.totalSeats <= 0) {
                throw new EntityNotValidException("Invalid value for parterre's total seats.");
            }
            seatGroup.setTotalSeats(this.totalSeats);
        } else if (this.colsNum == null || this.colsNum < 1 || this.rowsNum == null || this.rowsNum < 1) {
            throw new EntityNotValidException("Row and column numbers must be specified");
        }
        else {
            seatGroup.setRowsNum(this.rowsNum);
            seatGroup.setColsNum(this.colsNum);
            seatGroup.setTotalSeats(this.rowsNum * this.colsNum);
        }

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

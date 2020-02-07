package ktsnvt.tim1.DTOs;

public class SeatDTO {
    private Long id;
    private Integer rowNum;
    private Integer colNum;
    private boolean reserved;

    public SeatDTO() {
    }

    public SeatDTO(Long id, Integer rowNum, Integer colNum, boolean reserved) {
        this.id = id;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.reserved = reserved;
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

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}

package ktsnvt.tim1.DTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TicketDTO {
    private Integer rowNum;
    private Integer colNum;
    private String seatGroupName;
    private Double price;
    private ArrayList<LocalDateTime> eventDays;

    public TicketDTO() {
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

    public String getSeatGroupName() {
        return seatGroupName;
    }

    public void setSeatGroupName(String seatGroupName) {
        this.seatGroupName = seatGroupName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ArrayList<LocalDateTime> getEventDays() {
        return eventDays;
    }

    public void setEventDays(ArrayList<LocalDateTime> eventDays) {
        this.eventDays = eventDays;
    }
}

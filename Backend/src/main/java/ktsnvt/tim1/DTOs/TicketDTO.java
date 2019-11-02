package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.Ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class TicketDTO
{
    private Integer rowNum;
    private Integer colNum;
    private String seatGroupName;
    private Double price;
    private ArrayList<Date> eventDays;

    public TicketDTO() {
    }

    public TicketDTO(Ticket ticket) {
        this.rowNum = ticket.getRowNum();
        this.colNum = ticket.getColNum();
        this.seatGroupName = ticket.getEventSeatGroup().getSeatGroup().getName();
        this.price = ticket.getEventSeatGroup().getPrice();
        this.eventDays = ticket.getEventDays().stream().map(EventDay::getDate).collect(Collectors.toCollection(ArrayList::new));
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

    public ArrayList<Date> getEventDays() {
        return eventDays;
    }

    public void setEventDays(ArrayList<Date> eventDays) {
        this.eventDays = eventDays;
    }
}

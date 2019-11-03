package ktsnvt.tim1.DTOs;

public class NewTicketDTO {
    private Integer rowNum;
    private Integer colNum;
    private Long eventSeatGroupId;
    private Boolean allDayTicket;
    private Long eventDayId;

    public NewTicketDTO() {
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

    public Long getEventSeatGroupId() {
        return eventSeatGroupId;
    }

    public void setEventSeatGroupId(Long eventSeatGroupId) {
        this.eventSeatGroupId = eventSeatGroupId;
    }

    public Boolean getAllDayTicket() {
        return allDayTicket;
    }

    public void setAllDayTicket(Boolean allDayTicket) {
        this.allDayTicket = allDayTicket;
    }

    public Long getEventDayId() {
        return eventDayId;
    }

    public void setEventDayId(Long eventDayId) {
        this.eventDayId = eventDayId;
    }
}

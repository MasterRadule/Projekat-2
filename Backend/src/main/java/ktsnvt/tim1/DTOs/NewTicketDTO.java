package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotNull;

public class NewTicketDTO {
    private Long reservableSeatGroupId;
    private Long seatId;
    @NotNull
    private Boolean allDayTicket;

    public NewTicketDTO() {
    }

    public Long getReservableSeatGroupId() {
        return reservableSeatGroupId;
    }

    public void setReservableSeatGroupId(Long reservableSeatGroupId) {
        this.reservableSeatGroupId = reservableSeatGroupId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Boolean getAllDayTicket() {
        return allDayTicket;
    }

    public void setAllDayTicket(Boolean allDayTicket) {
        this.allDayTicket = allDayTicket;
    }
}

package ktsnvt.tim1.DTOs;

import java.time.LocalDateTime;

public class ReportDTO {
    private LocalDateTime date;
    private long ticketCount;
    private double earnings;

    public ReportDTO() {
    }

    public ReportDTO(LocalDateTime date, long ticketCount, double earnings) {
        this.date = date;
        this.ticketCount = ticketCount;
        this.earnings = earnings;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(long ticketCount) {
        this.ticketCount = ticketCount;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }
}

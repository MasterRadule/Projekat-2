package ktsnvt.tim1.DTOs;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportDTO {
    private List<Long> tickets = new ArrayList<>();
    private List<Double> earnings = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public ReportDTO(List<DailyReportDTO> dailyReports) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        for (DailyReportDTO dr : dailyReports) {
            labels.add(dr.getDate().format(formatter));
            tickets.add(dr.getTicketCount());
            earnings.add(dr.getEarnings());
        }
    }

    public List<Long> getTickets() {
        return tickets;
    }

    public void setTickets(List<Long> tickets) {
        this.tickets = tickets;
    }

    public List<Double> getEarnings() {
        return earnings;
    }

    public void setEarnings(List<Double> earnings) {
        this.earnings = earnings;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}

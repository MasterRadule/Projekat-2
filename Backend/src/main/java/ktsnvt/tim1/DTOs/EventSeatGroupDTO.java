package ktsnvt.tim1.DTOs;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Component
public class EventSeatGroupDTO {

    @NotNull(message = "Seat group ID cannot be null")
    private Long seatGroupID;

    @Positive
    private Double price;

    public EventSeatGroupDTO() {}

    public EventSeatGroupDTO(Long seatGroupID) {
        this.seatGroupID = seatGroupID;
    }

    public Long getSeatGroupID() {
        return seatGroupID;
    }

    public void setSeatGroupID(Long seatGroupID) {
        this.seatGroupID = seatGroupID;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

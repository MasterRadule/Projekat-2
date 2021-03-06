package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotNull;

public class NewReservationAndPaymentDTO {
    @NotNull
    private NewReservationDTO newReservationDTO;
    @NotNull
    private PaymentDTO paymentDTO;

    public NewReservationAndPaymentDTO() {
    }

    public NewReservationDTO getNewReservationDTO() {
        return newReservationDTO;
    }

    public void setNewReservationDTO(NewReservationDTO newReservationDTO) {
        this.newReservationDTO = newReservationDTO;
    }

    public PaymentDTO getPaymentDTO() {
        return paymentDTO;
    }

    public void setPaymentDTO(PaymentDTO paymentDTO) {
        this.paymentDTO = paymentDTO;
    }
}

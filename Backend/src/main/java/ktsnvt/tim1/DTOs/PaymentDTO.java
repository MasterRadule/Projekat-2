package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PaymentDTO {
    @NotBlank
    private String paymentID;
    @NotBlank
    private String payerID;

    public PaymentDTO() {
    }

    public PaymentDTO(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPayerID() {
        return payerID;
    }

    public void setPayerID(String payerID) {
        this.payerID = payerID;
    }
}

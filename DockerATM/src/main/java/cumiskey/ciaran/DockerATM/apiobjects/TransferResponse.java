package cumiskey.ciaran.DockerATM.apiobjects;

import java.math.BigDecimal;

public class TransferResponse extends BasicResponse {
    private BigDecimal transferAmount;
    private BigDecimal remainingBalance;

    public TransferResponse(final BigDecimal transferAmount, final BigDecimal remainingBalance) {
        super(ATMStatus.SUCCESS.getValue(), "Transfer successful");
        this.transferAmount = transferAmount;
        this.remainingBalance = remainingBalance;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}

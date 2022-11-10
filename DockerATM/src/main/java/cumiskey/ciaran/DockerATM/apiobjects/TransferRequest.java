package cumiskey.ciaran.DockerATM.apiobjects;

public class TransferRequest {
    private String accountNumber;
    private String accountPIN;
    private String transferAmount;
    private String payeeAccount;

    public TransferRequest(final String accountNumber, final String accountPIN, final String transferAmount,
                           final String payeeAccount) {
        this.accountNumber = accountNumber;
        this.accountPIN = accountPIN;
        this.transferAmount = transferAmount;
        this.payeeAccount = payeeAccount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountPIN() {
        return accountPIN;
    }

    public void setAccountPIN(String accountPIN) {
        this.accountPIN = accountPIN;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }
}

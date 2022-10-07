package cumiskey.ciaran.DockerATM.apiobjects;

import cumiskey.ciaran.DockerATM.model.Transaction;

import java.util.List;

public class ShowTransactionsResponse extends BasicResponse {
  private List<Transaction> transactions;

  public ShowTransactionsResponse() {
    super(ATMStatus.SUCCESS.getValue());
  }

  public ShowTransactionsResponse(final List<Transaction> transactions) {
    super(ATMStatus.SUCCESS.getValue());
    this.transactions = transactions;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}

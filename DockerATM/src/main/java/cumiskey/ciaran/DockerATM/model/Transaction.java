package cumiskey.ciaran.DockerATM.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {

  @Id @GeneratedValue
  private long id;

  @Column(name = "transaction_amount")
  private BigDecimal transactionAmount;

  @Column(name = "transaction_dt")
  private LocalDateTime transactionDateTime;

  @Column(name = "message")
  private String message;

  @Column(name = "customer_id")
  private String customer;

  public Transaction() {
    this.transactionAmount = BigDecimal.ZERO;
    this.transactionDateTime = LocalDateTime.now();
  }

  public Transaction(final Customer customer, final BigDecimal transactionAmount, final String message) {
    this.customer = customer.getAccountNumber();
    this.transactionAmount = transactionAmount;
    this.transactionDateTime = LocalDateTime.now();
    //Prevents overly-long messages being saved to the database.
    if(message.length() < 256) {
      this.message = message;
    } else {
      this.message = message.substring(0, 255);
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public BigDecimal getTransactionAmount() {
    return transactionAmount;
  }

  public void setTransactionAmount(BigDecimal transactionAmount) {
    this.transactionAmount = transactionAmount;
  }

  public LocalDateTime getTransactionDateTime() {
    return transactionDateTime;
  }

  public void setTransactionDateTime(LocalDateTime transactionDateTime) {
    this.transactionDateTime = transactionDateTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }
}

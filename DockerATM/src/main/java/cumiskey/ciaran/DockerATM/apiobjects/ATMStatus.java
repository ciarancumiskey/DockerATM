package cumiskey.ciaran.DockerATM.apiobjects;

public enum ATMStatus {
  SUCCESS(1),
  ACCOUNT_NOT_FOUND(-1),
  INCORRECT_PIN(-2),
  INSUFFICIENT_FUNDS(-3),
  ATM_CANNOT_FULFIL_WITHDRAWAL(-4);

  private int statusId;

  ATMStatus(int statusId) {
    this.statusId = statusId;
  }

  public int getStatusId() {
    return statusId;
  }

  public void setStatusId(int statusId) {
    this.statusId = statusId;
  }
}

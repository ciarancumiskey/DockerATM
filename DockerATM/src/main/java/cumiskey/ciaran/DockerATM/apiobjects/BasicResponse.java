package cumiskey.ciaran.DockerATM.apiobjects;

public class BasicResponse {
  private int statusCode;
  private String message;

  public BasicResponse(int statusCode) {
    this(statusCode, "");
  }

  public BasicResponse(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

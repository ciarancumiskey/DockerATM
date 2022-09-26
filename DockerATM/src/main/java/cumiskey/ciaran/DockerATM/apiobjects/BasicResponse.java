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
}

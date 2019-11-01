package response;

import com.google.gson.JsonElement;

public class Response {

  private Status status;
  private String message;
  private JsonElement devices;

  /* We can use Lombok to avoid below methods */

  public Response() {
    this.status = Status.SUCCESS;
  }

  public Response(Status status) {
    this.status = status;
  }

  public Response(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  public Response(Status status, JsonElement devices) {
    this.status = status;
    this.devices = devices;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public JsonElement getDevices() {
    return devices;
  }

  public void setDevices(JsonElement devices) {
    this.devices = devices;
  }
}

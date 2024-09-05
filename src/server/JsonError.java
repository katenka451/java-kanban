package server;

public class JsonError {

    private String message;

    public JsonError() {
        message = "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

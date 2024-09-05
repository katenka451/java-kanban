package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final int NOT_FOUND = -1;
    protected final int NUMBER_EXCEPTION = -2;
    protected Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange);
                break;
            default:
                handleUnknownRequest(exchange);
        }

    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 200);
    }

    protected void sendTextPosted(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 201);
    }

    protected void sendBadRequest(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 400);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 406);
    }

    protected void sendInternalError(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 500);
    }

    protected void sendNotImplemented(HttpExchange h, String text) throws IOException {
        sendTextResponse(h, text, 501);
    }

    protected int getIdFromURI(String path) {
        String[] splitStrings = path.split("/");
        if (splitStrings.length >= 3) {
            try {
                return Integer.parseInt(splitStrings[2]);
            } catch (NumberFormatException e) {
                return NUMBER_EXCEPTION;
            }
        } else {
            return NOT_FOUND;
        }
    }

    protected abstract void handleGetRequest(HttpExchange h) throws IOException;
    protected abstract void handlePostRequest(HttpExchange h) throws IOException;
    protected abstract void handleDeleteRequest(HttpExchange h) throws IOException;

    protected void handleUnknownRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();
        error.setMessage("Функция не поддерживается");
        sendNotImplemented(h, gson.toJson(error));
    }

    private void sendTextResponse(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}

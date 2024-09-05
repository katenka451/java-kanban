package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {

    public HistoryHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange h) throws IOException {
        var history = taskManager.getHistory();

        var response = gson.toJson(history, new TypeToken<List<Task>>() {
        }.getType());
        sendText(h, response);
    }

    @Override
    protected void handlePostRequest(HttpExchange h) throws IOException {
        handleUnknownRequest(h);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange h) throws IOException {
        handleUnknownRequest(h);
    }
}

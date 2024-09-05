package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedTasksHttpHandler extends BaseHttpHandler {

    public PrioritizedTasksHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange h) throws IOException {
        var prioritized = taskManager.getPrioritizedTasks();

        var response = gson.toJson(prioritized, new TypeToken<Set<Task>>() {
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

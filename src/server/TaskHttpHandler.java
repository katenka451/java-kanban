package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import exception.ObjectNotFound;
import exception.TasksOverlappedException;
import model.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int taskId = getIdFromURI(h.getRequestURI().getPath());
        if (taskId == NUMBER_EXCEPTION) {
            error.setMessage("Запрошен некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        if (taskId != NOT_FOUND) {
            try {
                Task task = taskManager.getTaskById(taskId);
                sendText(h, gson.toJson(task, Task.class));
            } catch (ObjectNotFound e) {
                error.setMessage(e.getMessage());
                sendNotFound(h, gson.toJson(error));
            }
        } else {
            var tasks = taskManager.getTasks();
            sendText(h, gson.toJson(tasks, new TypeToken<List<Task>>() {
            }.getType()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();
        Task task; //По ТЗ непонятно, предполагаем, что задача всегда обновляется/создается только одна

        int taskId = getIdFromURI(h.getRequestURI().getPath());
        if (taskId == NUMBER_EXCEPTION) {
            error.setMessage("Задан некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        try {
            task = gson.fromJson(new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Task.class);
        } catch (JsonSyntaxException e) {
            error.setMessage("Не удалось сформировать объект");
            sendInternalError(h, gson.toJson(error));
            return;
        } catch (Exception e) {
            error.setMessage("Неизвестная ошибка десереализации");
            sendInternalError(h, gson.toJson(error));
            return;
        }

        if (taskId == NOT_FOUND) {
            try {
                taskManager.createTask(task);
                sendTextPosted(h, gson.toJson(task, Task.class));
            } catch (TasksOverlappedException e) {
                error.setMessage(e.getMessage());
                sendHasInteractions(h, gson.toJson(error));
            }
        } else {
            try {
                taskManager.updateTask(task);
                sendTextPosted(h, gson.toJson(task, Task.class));
            } catch (TasksOverlappedException e) {
                error.setMessage(e.getMessage());
                sendHasInteractions(h, gson.toJson(error));
            }
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int taskId = getIdFromURI(h.getRequestURI().getPath());
        if (taskId == NUMBER_EXCEPTION || taskId == NOT_FOUND) {
            error.setMessage("Задан некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        taskManager.deleteTaskById(taskId);
        sendText(h, "");
    }

}

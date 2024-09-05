package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import exception.ObjectNotFound;
import exception.TasksOverlappedException;
import model.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHttpHandler extends BaseHttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int subTaskId = getIdFromURI(h.getRequestURI().getPath());
        if (subTaskId == NUMBER_EXCEPTION) {
            error.setMessage("Запрошен некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        if (subTaskId != NOT_FOUND) {
            try {
                Subtask subtask = taskManager.getSubtaskById(subTaskId);
                sendText(h, gson.toJson(subtask, Subtask.class));
            } catch (ObjectNotFound e) {
                error.setMessage(e.getMessage());
                sendNotFound(h, gson.toJson(error));
            }
        } else {
            var subtasks = taskManager.getSubtasks();
            sendText(h, gson.toJson(subtasks, new TypeToken<List<Subtask>>() {
            }.getType()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();
        Subtask subtask; //По ТЗ непонятно, предполагаем, что задача всегда обновляется/создается только одна

        int subtaskId = getIdFromURI(h.getRequestURI().getPath());
        if (subtaskId == NUMBER_EXCEPTION) {
            error.setMessage("Задан некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        try {
            subtask = gson.fromJson(new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Subtask.class);
        } catch (JsonSyntaxException e) {
            error.setMessage("Не удалось сформировать объект");
            sendInternalError(h, gson.toJson(error));
            return;
        } catch (Exception e) {
            error.setMessage("Неизвестная ошибка десереализации");
            sendInternalError(h, gson.toJson(error));
            return;
        }

        if (subtaskId == NOT_FOUND) {
            try {
                taskManager.createSubtask(subtask);
                sendTextPosted(h, gson.toJson(subtask, Subtask.class));
            } catch (TasksOverlappedException e) {
                error.setMessage(e.getMessage());
                sendHasInteractions(h, gson.toJson(error));
            }
        } else {
            try {
                taskManager.updateSubtask(subtask);
                sendTextPosted(h, gson.toJson(subtask, Subtask.class));
            } catch (TasksOverlappedException e) {
                error.setMessage(e.getMessage());
                sendHasInteractions(h, gson.toJson(error));
            }
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int subtaskId = getIdFromURI(h.getRequestURI().getPath());
        if (subtaskId == NUMBER_EXCEPTION || subtaskId == NOT_FOUND) {
            error.setMessage("Задан некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        taskManager.deleteSubtaskById(subtaskId);
        sendText(h, "");
    }
}

package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import exception.ObjectNotFound;
import exception.TasksOverlappedException;
import model.Epic;
import model.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int epicId = getIdFromURI(h.getRequestURI().getPath());
        if (epicId == NUMBER_EXCEPTION) {
            error.setMessage("Запрошен некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        if (epicId != NOT_FOUND) {
            try {
                Epic epic = taskManager.getEpicById(epicId);
                //Возможны варианты, запрашивают эпик или список его подзадач
                String[] splitStrings = h.getRequestURI().getPath().split("/");
                boolean isSubtaskPathFound = Arrays.stream(splitStrings).toList().stream()
                        .anyMatch(pathPart -> pathPart.contains("subtasks"));
                if (isSubtaskPathFound) {
                    sendText(h, gson.toJson(epic.getSubtasks(), new TypeToken<List<Subtask>>() {
                    }.getType()));
                } else {
                    sendText(h, gson.toJson(epic, Epic.class));
                }
            } catch (ObjectNotFound e) {
                error.setMessage(e.getMessage());
                sendNotFound(h, gson.toJson(error));
            }
        } else {
            var epics = taskManager.getEpics();
            sendText(h, gson.toJson(epics, new TypeToken<List<Epic>>() {
            }.getType()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();
        Epic epic; //По ТЗ непонятно, предполагаем, что задача всегда обновляется/создается только одна

        int epicId = getIdFromURI(h.getRequestURI().getPath());
        if (epicId != NOT_FOUND) {
            error.setMessage("Невозможно обработать запрос");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        try {
            epic = gson.fromJson(new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Epic.class);
        } catch (JsonSyntaxException e) {
            error.setMessage("Не удалось сформировать объект");
            sendInternalError(h, gson.toJson(error));
            return;
        } catch (Exception e) {
            error.setMessage("Неизвестная ошибка десереализации");
            sendInternalError(h, gson.toJson(error));
            return;
        }

        try {
            taskManager.createEpic(epic);
            sendTextPosted(h, gson.toJson(epic, Epic.class));
        } catch (TasksOverlappedException e) {
            error.setMessage(e.getMessage());
            sendHasInteractions(h, gson.toJson(error));
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange h) throws IOException {
        JsonError error = new JsonError();

        int epicId = getIdFromURI(h.getRequestURI().getPath());
        if (epicId == NUMBER_EXCEPTION || epicId == NOT_FOUND) {
            error.setMessage("Задан некорректный id");
            sendBadRequest(h, gson.toJson(error));
            return;
        }

        taskManager.deleteEpicById(epicId);
        sendText(h, "");
    }
}

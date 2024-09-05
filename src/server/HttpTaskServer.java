package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerReadException;
import model.Status;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать сервер");
        }

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Status.class, new StatusTypeAdapter())
                .create();

        createContext(this.taskManager, gson);
    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(10);
    }

    private void createContext(TaskManager taskManager, Gson gson) {
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHttpHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHttpHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHttpHandler(taskManager, gson));
    }

    public static void main(String[] args) {
        TaskManager taskManager;

        try {
            taskManager = Managers.getFileBackedManager();
        } catch (ManagerReadException e) {
            throw new RuntimeException("Не удалось создать менеджер задач");
        }

        HttpTaskServer httpServer = new HttpTaskServer(taskManager);
        httpServer.start();
    }

}

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getPrioritizedList() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1");
        task.setDuration(Duration.ofMinutes(25));
        task.setStartTime(LocalDateTime.parse("15.07.2024 21:21", formatter));

        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing history");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются (тестирование подзадач)");

        // создаём подзадачу
        Subtask subtask = new Subtask("Test subtask 1", "Test subtask 1 for epic 1", epicsFromManager.getFirst().getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.parse("05.09.2024 12:20", formatter));

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // создаём подзадачу
        Subtask subtask_2 = new Subtask("Test subtask 2", "Test subtask 2 for epic 1", epicsFromManager.getFirst().getId());
        subtask_2.setDuration(Duration.ofMinutes(5));
        subtask_2.setStartTime(LocalDateTime.parse("15.08.2024 21:21", formatter));

        // конвертируем её в JSON
        subtaskJson = gson.toJson(subtask_2);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // создаём подзадачу
        Subtask subtask_3 = new Subtask("Test subtask 3", "Test subtask 3 for epic 1", epicsFromManager.getFirst().getId());

        // конвертируем её в JSON
        subtaskJson = gson.toJson(subtask_3);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        Set<Task> sortedTasks = manager.getPrioritizedTasks();
        assertEquals(3, sortedTasks.size(), "Некорректный сортированный список задач");
        assertEquals(LocalDateTime.parse("15.07.2024 21:21", formatter),
                sortedTasks.stream().findFirst().get().getStartTime(),
                "Некорректная сортировка задач");

    }
}
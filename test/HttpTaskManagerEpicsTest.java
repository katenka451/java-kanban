import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

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
    public void testAddEpic() throws IOException, InterruptedException {
//создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing epics");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются (тестирование подзадач)");

        // создаём подзадачу
        Subtask subtask_1 = new Subtask("Test 1", "Testing subtask 1", epicsFromManager.getFirst().getId());
        subtask_1.setDuration(Duration.ofMinutes(5));
        subtask_1.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask_1);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // создаём подзадачу
        Subtask subtask_2 = new Subtask("Test 2", "Testing subtask 2", epicsFromManager.getFirst().getId());
        subtask_2.setDuration(Duration.ofMinutes(5));

        // конвертируем её в JSON
        subtaskJson = gson.toJson(subtask_2);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создался один эпик с корректным количеством подзадач
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic 1", epicsFromManager.getFirst().getTaskName(), "Некорректное имя эпика");
        assertEquals(2, epicsFromManager.getFirst().getSubtasks().size(), "Некорректное количество подзадач у эпика");
    }

    @Test
    public void testGetEpicsList() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing epics");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //создаём эпик
        Epic epic_2 = new Epic("Test epic 2", "Testing epics");
        // конвертируем её в JSON
        epicJson = gson.toJson(epic_2);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //Получаем список эпиков
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за список эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> epicsList = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertNotNull(epicsList, "Эпики не возвращаются");
        assertEquals(2, epicsList.size(), "Некорректное количество эпиков");
        assertEquals("Test epic 1", epicsList.getFirst().getTaskName(), "Некорректное имя эпика");
        assertEquals("Test epic 2", epicsList.getLast().getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing epics");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //создаём эпик
        Epic epic_2 = new Epic("Test epic 2", "Testing epics");
        // конвертируем её в JSON
        epicJson = gson.toJson(epic_2);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //Получаем список эпиков
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за список эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> epicsList = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertNotNull(epicsList, "Эпики не возвращаются");
        assertEquals(2, epicsList.size(), "Некорректное количество эпиков");

        url = URI.create("http://localhost:8080/epics/" + epicsList.getLast().getId());
        // Получаем эпик по ID
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение эпика по id
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Test epic 2", epic.getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing epics");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создался один эпик с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic 1", epicsFromManager.getFirst().getTaskName(), "Некорректное имя эпика");

        url = URI.create("http://localhost:8080/epics/" + epicsFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление эпиков
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        epicsFromManager = manager.getEpics();
        assertTrue(epicsFromManager.isEmpty(), "Эпики не удаляются");
    }
}
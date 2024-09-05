import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
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

public class HttpTaskManagerSubtasksTest {

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
    public void testAddSubtask() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing subtasks");
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
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", epicsFromManager.getFirst().getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна подзадача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getTaskName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing subtasks");
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
        Subtask subtask = new Subtask("Test 2", "Testing task 2", epicsFromManager.getFirst().getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getTaskName(), "Некорректное имя подзадачи");

        //Обновляем подзадачу
        subtask.setId(subtasksFromManager.getFirst().getId());
        subtask.setTaskStatus(Status.DONE);
        // конвертируем её в JSON
        subtaskJson = gson.toJson(subtask);
        // создаём запрос
        url = URI.create("http://localhost:8080/subtasks/" + subtasksFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        // вызываем рест, отвечающий за обновление подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        //Проверяем, что статус подзадачи обновился
        subtasksFromManager = manager.getSubtasks();
        assertEquals(Status.DONE, subtasksFromManager.getFirst().getTaskStatus(), "Некорректный статус подзадачи");

    }

    @Test
    public void testGetSubtasksList() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing subtasks");
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
        Subtask subtask_1 = new Subtask("Test 1", "Testing task 1", epicsFromManager.getFirst().getId());
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
        Subtask subtask_2 = new Subtask("Test 2", "Testing task 2", epicsFromManager.getFirst().getId());
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

        //Получаем список подзадач
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение списка подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksList = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(subtasksList, "Подзадачи не возвращаются");
        assertEquals(2, subtasksList.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", subtasksList.getFirst().getTaskName(), "Некорректное имя подзадачи");
        assertEquals("Test 2", subtasksList.getLast().getTaskName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing subtasks");
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
        Subtask subtask_1 = new Subtask("Test 1", "Testing task 1", epicsFromManager.getFirst().getId());
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
        Subtask subtask_2 = new Subtask("Test 2", "Testing task 2", epicsFromManager.getFirst().getId());
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

        //Получаем список подзадач
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение списка подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksList = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(subtasksList, "Подадачи не возвращаются");
        assertEquals(2, subtasksList.size(), "Некорректное количество подзадач");

        url = URI.create("http://localhost:8080/subtasks/" + subtasksList.getLast().getId());
        // Получаем задачу по ID
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за получение подзадачи по id
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("Test 2", subtask.getTaskName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        //создаём эпик
        Epic epic = new Epic("Test epic 1", "Testing subtasks");
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
        Subtask subtask = new Subtask("Test 2", "Testing task 2", epicsFromManager.getFirst().getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getTaskName(), "Некорректное имя подзадачи");

        url = URI.create("http://localhost:8080/subtasks/" + subtasksFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление подзадач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        subtasksFromManager = manager.getSubtasks();
        assertTrue(subtasksFromManager.isEmpty(), "Подадачи не удаляются");
    }
}
import exception.ManagerReadException;
import taskmanager.FileBackedTaskManager;
import taskmanager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File tmpFile;

    @BeforeEach
    void beforeEach() throws Exception {
        this.tmpFile = File.createTempFile("TasksList", ".CSV");
        assertNotNull(tmpFile, "Файл не создан");
        assertTrue(tmpFile.exists(), "Файл не создан");
    }

    @Test
    void loadAndSaveEmptyFile() throws Exception {
        var taskManager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());
        assertNotNull(taskManager, "Не удалось создать менеджер задач из файла");

        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        taskManager.createTask(task1);
        taskManager.clearTasks();

        String content;
        content = Files.readString(tmpFile.toPath());
        assertEquals(content, FileBackedTaskManager.CSV_HEADER + System.lineSeparator());
    }

    @Test
    void saveSeveralTasks() throws Exception {
        var taskManager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());
        assertNotNull(taskManager, "Не удалось создать менеджер задач из файла");
        generateTestData(taskManager);

        assertNotEquals(tmpFile.length(), 0, "Данные не были записаны в файл");

        String content;
        content = Files.readString(tmpFile.toPath());
        String[] splitContent = content.split(System.lineSeparator());

        assertEquals(splitContent.length, 4, "Не все задачи записаны в файл");
    }

    @Test
    void loadSeveralTasks() throws Exception {
        saveSeveralTasks();

        var taskManager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());
        assertNotNull(taskManager, "Не удалось создать менеджер задач из файла");

        int allTasksAmount =
                taskManager.getTasks().size() +
                        taskManager.getEpics().size() +
                        taskManager.getSubtasks().size();

        assertEquals(allTasksAmount, 3, "Не все задачи загружены из файла");
    }

    @Test
    void isManagersFromSameFileEqual() {
        TaskManager newTaskManager = new FileBackedTaskManager(tmpFile.toPath());
        generateTestData(newTaskManager);

        var taskManagerFromFile = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

        var tasksSorted = newTaskManager.getTasks().stream().sorted(Comparator.comparingInt(Task::getId)).toList();
        var tasksFromFileSorted = taskManagerFromFile.getTasks().stream().sorted(Comparator.comparingInt(Task::getId)).toList();
        assertEquals(tasksSorted, tasksFromFileSorted, "Списки задач не совпадают");

        var epicsSorted = newTaskManager.getEpics().stream().sorted(Comparator.comparingInt(Epic::getId)).toList();
        var epicsFromFileSorted = taskManagerFromFile.getEpics().stream().sorted(Comparator.comparingInt(Epic::getId)).toList();
        assertEquals(epicsSorted, epicsFromFileSorted, "Списки эпиков не совпадают");

        var subtasksSorted = newTaskManager.getSubtasks().stream().sorted(Comparator.comparingInt(Subtask::getId)).toList();
        var subtasksFromFileSorted = taskManagerFromFile.getSubtasks().stream().sorted(Comparator.comparingInt(Subtask::getId)).toList();
        assertEquals(subtasksSorted, subtasksFromFileSorted, "Списки подзадач не совпадают");
    }

    @Test
    void testFileReadException() {
        TaskManager newTaskManager = new FileBackedTaskManager(tmpFile.toPath());
        generateTestData(newTaskManager);

        assertThrows(ManagerReadException.class, () -> FileBackedTaskManager.loadFromFile(Path.of("M:/Dummy.csv")),
                    "Ошибка чтения из файла");
    }

    private void generateTestData(TaskManager taskManager) {
        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        taskManager.createTask(task1);

        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1 для эпика 1",
                epic1.getId());
        taskManager.createSubtask(subtask1ForEpic1);

    }
}

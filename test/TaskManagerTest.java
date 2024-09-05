import taskmanager.FileBackedTaskManager;
import taskmanager.Managers;
import taskmanager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    @ParameterizedTest
    @MethodSource("provideParameters")
    void fullTestScenario(TaskManager taskManager) {
        generateTestData(taskManager);

        assertNotNull(taskManager.getTasks().getFirst(), "Задача не создана");
        assertNotNull(taskManager.getEpics().getFirst(), "Эпик не создан");
        assertNotNull(taskManager.getSubtasks().getFirst(), "Подзадача не создана");

        assertEquals(taskManager.getPrioritizedTasks().stream().toList().getFirst().getStartTime(),
                LocalDateTime.parse("2024-05-22T21:21:21"),
                "Некорректная сортировка задач по времени начала");

        assertEquals(taskManager.getEpics().getFirst().getTaskStatus(), Status.IN_PROGRESS,
                "Некорректный расчет статуса эпика");

        assertEquals(taskManager.getEpics().getFirst().getSubtasks().size(), 3,
                "Некорректный список подзадач эпика");

        assertTrue(taskManager.getSubtasks().stream()
                        .allMatch(subtask -> subtask.getEpicId() != 0),
                "Не все подзадачи имеют эпик");

        Task task3 = new Task(
                "Задача 3",
                "Тестовая задача 3");
        task3.setStartTime(LocalDateTime.parse("2024-07-16T21:21:21"));
        task3.setDuration(Duration.ofDays(1));
        assertTrue(taskManager.isTasksOverlapped(task3), "Некорректное определение пересечения интервалов дат");
        task3.setStartTime(LocalDateTime.parse("2024-07-19T21:21:21"));
        assertFalse(taskManager.isTasksOverlapped(task3), "Некорректное определение пересечения интервалов дат");

        taskManager.createTask(task3);
        task3.setDuration(Duration.ofDays(2));
        taskManager.updateTask(task3);
        assertEquals(taskManager.getTaskById(task3.getId()).getDuration().toDays(), 2,
                "Задачи обновляются некорректно");

        taskManager.getEpics().getFirst().getSubtasks().forEach(subtask -> {
            subtask.setTaskStatus(Status.DONE);
            taskManager.getEpics().getFirst().updateSubtask(subtask);
        });

        assertEquals(taskManager.getEpics().getFirst().getTaskStatus(), Status.DONE,
                "Некорректный расчет статуса эпика");

        taskManager.deleteEpicById(taskManager.getEpics().getFirst().getId());
        assertTrue(taskManager.getEpics().isEmpty(), "Некорректное удаление эпиков");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Некорректное удаление подзадач");

    }

    private void generateTestData(TaskManager taskManager) {
        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        task1.setStartTime(LocalDateTime.parse("2024-07-15T21:21:21"));
        task1.setDuration(Duration.ofDays(3));
        taskManager.createTask(task1);

        Task task2 = new Task(
                "Задача 2",
                "Тестовая задача 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1 для эпика 1",
                epic1.getId());
        subtask1ForEpic1.setStartTime(LocalDateTime.parse("2024-05-22T21:21:21"));
        subtask1ForEpic1.setDuration(Duration.ofDays(3));
        taskManager.createSubtask(subtask1ForEpic1);

        Subtask subtask2ForEpic1 = new Subtask(
                "Подзадача 1.2",
                "Тестовая подзадача 2 для эпика 1",
                epic1.getId());
        subtask2ForEpic1.setStartTime(LocalDateTime.parse("2024-08-22T21:21:21"));
        subtask2ForEpic1.setDuration(Duration.ofDays(1));
        taskManager.createSubtask(subtask2ForEpic1);

        Subtask subtask3ForEpic1 = new Subtask(
                "Подзадача 1.3",
                "Тестовая подзадача 3 для эпика 1",
                epic1.getId());
        subtask3ForEpic1.setTaskStatus(Status.DONE);
        subtask3ForEpic1.setStartTime(LocalDateTime.parse("2024-08-15T21:21:21"));
        subtask3ForEpic1.setDuration(Duration.ofDays(5));
        taskManager.createSubtask(subtask3ForEpic1);
    }

    private static Stream<Arguments> provideParameters() throws Exception {
        File tmpFile = File.createTempFile("TasksList", ".CSV");
        assertNotNull(tmpFile, "Файл не создан");
        assertTrue(tmpFile.exists(), "Файл не создан");

        return Stream.of(Arguments.of(Managers.getDefault()),
                Arguments.of(FileBackedTaskManager.loadFromFile(tmpFile.toPath())));
    }
}

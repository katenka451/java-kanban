import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void tasksShouldBeEqual() {
        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        taskManager.createTask(task1);
        task1.setId(1);

        Task task2 = new Task(
                "Задача 2",
                "Тестовая задача 2");
        taskManager.createTask(task2);
        task2.setId(1);

        Assertions.assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void epicsShouldBeEqual() {
        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic1);
        epic1.setId(1);

        Epic epic2 = new Epic(
                "Эпик 2",
                "Тестовый эпик 2");
        taskManager.createEpic(epic2);
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2, "Эпики не совпадают");
    }

    @Test
    void subtasksShouldBeEqual() {
        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1.1",
                epic1.getId());
        taskManager.createSubtask(subtask1_1);
        subtask1_1.setId(1);

        Subtask subtask1_2 = new Subtask(
                "Подзадача 1.2",
                "Тестовая подзадача 1.2",
                epic1.getId());
        taskManager.createSubtask(subtask1_2);
        subtask1_2.setId(1);

        Assertions.assertEquals(subtask1_1, subtask1_2, "Подзадачи не совпадают");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Тест addNewTask", "Тест добавления новой задачи");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "Подзадача 1",
                "Тестовая подзадача 1",
                epic.getId());
        taskManager.createSubtask(subtask1);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void isSubtasksDeletedFromEpics() {
        Epic epic = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(
                "Подзадача 1",
                "Тестовая подзадача 1",
                epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(
                "Подзадача 2",
                "Тестовая подзадача 2",
                epic.getId());
        taskManager.createSubtask(subtask2);

        int deletedSubtaskId = subtask1.getId();
        taskManager.deleteSubtaskById(deletedSubtaskId);
        assertFalse(epic.hasSubtask(deletedSubtaskId), "В эпике остаются неактуальные сабтаски");
    }

    @Test
    void isEpicDeletedFromHistoryWithSubtasks() {
        Epic epic = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(
                "Подзадача 1",
                "Тестовая подзадача 1",
                epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(
                "Подзадача 2",
                "Тестовая подзадача 2",
                epic.getId());
        taskManager.createSubtask(subtask2);

        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(history.size(), 3, "В историю не сохраняются данные эпиков и/или сабтасков");

        taskManager.deleteEpicById(epic.getId());
        history = taskManager.getHistory();
        assertEquals(history.size(), 0, "Из истории не удаляются данные эпиков и/или сабтасков");
    }

    @Test
    void isTasksAddedToHistoryInCorrectOrder() {
        Task task1 = new Task("Задача 1", "Тестовая задача 1");
        taskManager.createTask(task1);

        Task task2 = new Task("Задача 2", "Тестовая задача 2");
        taskManager.createTask(task2);

        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(history.getFirst().getId(), task1.getId(), "История запросов сохраняется в некорректном порядке");

    }

    @Test
    void checkEpicStatusCalculation() {
        Epic epic = new Epic(
                "Эпик",
                "Тестовый эпик");
        taskManager.createEpic(epic);
        Subtask subtask1ForEpic1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1 для эпика 1",
                epic.getId());
        subtask1ForEpic1.setStartTime(LocalDateTime.now());
        subtask1ForEpic1.setDuration(Duration.ofDays(3));
        taskManager.createSubtask(subtask1ForEpic1);

        Subtask subtask2ForEpic1 = new Subtask(
                "Подзадача 1.2",
                "Тестовая подзадача 2 для эпика 1",
                epic.getId());
        subtask2ForEpic1.setStartTime(LocalDateTime.parse("2024-07-15T21:21:21"));
        subtask2ForEpic1.setDuration(Duration.ofDays(1));
        taskManager.createSubtask(subtask2ForEpic1);

        Subtask subtask3ForEpic1 = new Subtask(
                "Подзадача 1.3",
                "Тестовая подзадача 3 для эпика 1",
                epic.getId());
        subtask3ForEpic1.setStartTime(LocalDateTime.parse("2024-08-15T21:21:21"));
        subtask3ForEpic1.setDuration(Duration.ofDays(5));
        taskManager.createSubtask(subtask3ForEpic1);

        assertEquals(taskManager.getEpicById(epic.getId()).getTaskStatus(), Status.NEW,
                "Все подзадачи со статусом NEW. Статус эпика: " + epic.getTaskStatus());

        subtask3ForEpic1.setTaskStatus(Status.DONE);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask3ForEpic1);
        assertEquals(taskManager.getEpicById(epic.getId()).getTaskStatus(), Status.IN_PROGRESS,
                "Подзадачи NEW и DONE. Статус эпика: " + epic.getTaskStatus());

        subtask1ForEpic1.setTaskStatus(Status.IN_PROGRESS);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask1ForEpic1);
        subtask3ForEpic1.setTaskStatus(Status.IN_PROGRESS);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask3ForEpic1);
        assertEquals(taskManager.getEpicById(epic.getId()).getTaskStatus(), Status.IN_PROGRESS,
                "Подзадачи IN_PROGRESS. Статус эпика: " + epic.getTaskStatus());

        subtask1ForEpic1.setTaskStatus(Status.DONE);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask1ForEpic1);
        subtask2ForEpic1.setTaskStatus(Status.DONE);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask2ForEpic1);
        subtask3ForEpic1.setTaskStatus(Status.DONE);
        taskManager.getEpicById(epic.getId()).updateSubtask(subtask3ForEpic1);
        assertEquals(taskManager.getEpicById(epic.getId()).getTaskStatus(), Status.DONE,
                "Все подзадачи DONE. Статус эпика: " + epic.getTaskStatus());
    }

}
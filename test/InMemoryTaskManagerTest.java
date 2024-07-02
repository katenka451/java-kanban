import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                "Task1",
                "Test task1");
        taskManager.createTask(task1);
        task1.setId(1);

        Task task2 = new Task(
                "Task2",
                "Test task2");
        taskManager.createTask(task2);
        task2.setId(1);

        Assertions.assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void epicsShouldBeEqual() {
        Epic epic1 = new Epic(
                "Epic1",
                "Test epic1");
        taskManager.createEpic(epic1);
        epic1.setId(1);

        Epic epic2 = new Epic(
                "Epic2",
                "Test epic2");
        taskManager.createEpic(epic2);
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2, "Эпики не совпадают");
    }

    @Test
    void subtasksShouldBeEqual() {
        Epic epic1 = new Epic(
                "Epic1",
                "Test epic1");
        taskManager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask(
                "Subtask 1.1",
                "Test subtask 1.1",
                epic1.getId());
        taskManager.createSubtask(subtask1_1);
        subtask1_1.setId(1);

        Subtask subtask1_2 = new Subtask(
                "Subtask 1.2",
                "Test subtask 1.2",
                epic1.getId());
        taskManager.createSubtask(subtask1_2);
        subtask1_2.setId(1);

        Assertions.assertEquals(subtask1_1, subtask1_2, "Подзадачи не совпадают");
    }

    /*Задание по тестированию "проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи" и
    аналогичное про subtask предполагает, что, возможно, нужно использовать дженерики в TaskManager. И
    сделать универсальные методы по чтению/добавлению/обновлению задач/эпиков/подзадач.
    При прошлом варианте реализации добавить Epic как подзадачу физически нельзя.
    Поскольку правки значительные, а я, возможно, делаю ошибочное предположение, пока подожду комментариев*/

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
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
                "Epic1",
                "Test epic1");
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
                "Epic1",
                "Test epic1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask 1",
                "Test subtask 1",
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

    /*Задание по тестированию "проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера"
    В текущей реализации (одобренной на прошлом 4-м спринте), не было функционала, который реализовывал бы возможность
    как задавать Id задачи извне, так и получать сгенерированный Id. Пока оставила, как есть. */

}
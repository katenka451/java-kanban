import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();

        task = new Task(
                "Задача 1",
                "Тестовая задача 1");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void isTaskPreviousVersionSaved() {
        task.setTaskStatus(Status.IN_PROGRESS);
        historyManager.add(task);
        task.setTaskStatus(Status.DONE);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(Status.IN_PROGRESS, history.getLast().getTaskStatus(), "В историю не сохраняется предыдущая версия данных");

    }

    @Test
    void isTasksUnique() {
        task.setTaskStatus(Status.IN_PROGRESS);
        historyManager.add(task);
        task.setTaskStatus(Status.DONE);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(history.size(), 1, "В историю сохраняются дубликаты");

        task.setId(1);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(history.size(), 2, "В историю не сохраняются задачи с разными ID");

        assertNotEquals(history.get(0).getId(), history.get(1).getId(), "В историю сохраняются ссылки на оригинальные объекты");
    }

}
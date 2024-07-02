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
                "Task1",
                "Test task1");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void isPreviousVersionSaved() {
        task.setTaskStatus(Status.IN_PROGRESS);
        historyManager.add(task);
        task.setTaskStatus(Status.DONE);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(Status.IN_PROGRESS, history.getLast().getTaskStatus(), "В историю не сохраняется предыдущая версия данных");

    }
}
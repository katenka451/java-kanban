import taskmanager.HistoryManager;
import taskmanager.Managers;
import taskmanager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager, "Объект impl.TaskManager не найден");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "Объект impl.HistoryManager не найден");
    }
}
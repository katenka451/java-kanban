import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final List<Task> historyList;

    public InMemoryHistoryManager() {
        historyList = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        Task taskClone;

        switch (task) {
            case Epic epic -> taskClone = new Epic(epic);
            case Subtask subtask -> taskClone = new Subtask(subtask);
            default -> taskClone = new Task(task);
        }

        if (historyList.size() >= 10) {
            historyList.removeLast();
        }

        historyList.addFirst(taskClone);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}

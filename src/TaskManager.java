import java.util.List;
import java.util.Set;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Set<Task> getPrioritizedTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    boolean isTaskExists(int id);

    boolean isEpicExists(int id);

    boolean isSubtaskExists(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Task> getHistory();

    boolean isTasksOverlapped(Task task);

}

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, Epic> epicsMap;
    private int taskId;

    public TaskManager() {
        taskId = 1;
        tasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
    }

    public void createTask(Task task) {
        if (task == null) {
            return;
        }
        int newId = this.getAndIncrementId();
        task.setId(newId);
        tasksMap.put(newId, task);
    }

    public void createEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        int newId = this.getAndIncrementId();
        epic.setId(newId);
        epicsMap.put(newId, epic);
    }

    public void createSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        int newId = this.getAndIncrementId();
        subtask.setId(newId);
        epicsMap.get(subtask.getEpicId()).addSubtask(newId, subtask);
    }

    public List<Task> getTasks() {
        return tasksMap.values().stream().toList();
    }

    public List<Epic> getEpics() {
        return epicsMap.values().stream().toList();
    }

    public List<Subtask> getSubtasks() {
        return this.getSubtasksMap().values().stream().toList();
    }

    public Task getTaskById(int id) {
        return tasksMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();
        return subtasksMap.get(id);
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return epicsMap.get(epicId).getSubtasks();
    }

    public void clearTasks() {
        tasksMap.clear();
    }

    public void clearEpics() {
        epicsMap.clear();
    }

    public void clearSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.clearSubtasks();
        }
    }

    public void deleteTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            return;
        }

        tasksMap.remove(id);
    }

    public void deleteEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            return;
        }

        epicsMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();

        if (!subtasksMap.containsKey(id)) {
            return;
        }

        int epicId = subtasksMap.get(id).getEpicId();
        epicsMap.get(epicId).deleteSubtask(id);
    }

    public void updateTask(Task task) {
        if (tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (!epicsMap.containsKey(subtask.getEpicId())) {
            return;
        }

        epicsMap.get(subtask.getEpicId()).updateSubtask(subtask);
    }

/*  Ранее было реализовано через передачу результата getAndIncrementId в конструктор Task
    и дочерних классов, поскольку так мы имели бы возможность только единожды задавать номер задачи
    (при создании).
    При текущей реализации у нас есть setter, и номер задачи может быть изменен извне в любой момент. */
    private int getAndIncrementId() {
        return taskId++;
    }

    private Map<Integer, Subtask> getSubtasksMap() {
        Map<Integer, Subtask> subtasks = new HashMap<>();
        for (Epic epic : epicsMap.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
        return subtasks;
    }
}

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

    public int getAndIncrementId() {
        return taskId++;
    }

    public <T> void addIntoMap(Map<Integer, T> map, T t) {
        var task = (Task)t;
        if (task == null || map.containsKey(task.getId()) || task.getId() == 0) {
            return;
        }

        map.put(task.getId(), t);
    }

    public void createTask(Task task) {
        addIntoMap(tasksMap, task);
    }

    public void createEpic(Epic epic) {
        addIntoMap(epicsMap, epic);
    }

    public void createSubtask(Subtask subtask) {
        if (subtask == null || !epicsMap.containsKey(subtask.getEpicId()) || subtask.getId() == 0) {
            return;
        }

        epicsMap.get(subtask.getEpicId()).addSubtask(subtask);
    }

    public Map<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public Map<Integer, Epic> getEpicsMap() {
        return epicsMap;
    }

    public Map<Integer, Subtask> getSubtasksMap() {
        Map<Integer, Subtask> subtasks = new HashMap<>();
        for (Epic epic : epicsMap.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
        return subtasks;
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

    public List<Subtask> getSubtasksForEpic(int epicId) {
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


}

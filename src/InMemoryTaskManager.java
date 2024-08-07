import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, Epic> epicsMap;
    private final HistoryManager historyManager;
    private int taskId;

    public InMemoryTaskManager() {
        taskId = 1;
        tasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) {
        if (task == null) {
            return;
        }

        int newId = getAndIncrementId();
        task.setId(newId);
        tasksMap.put(newId, task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        int newId = getAndIncrementId();
        epic.setId(newId);
        epicsMap.put(newId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask == null || epicsMap.get(subtask.getEpicId()) == null) {
            return;
        }

        int newId = getAndIncrementId();
        subtask.setId(newId);
        epicsMap.get(subtask.getEpicId()).addSubtask(newId, subtask);
    }

    @Override
    public List<Task> getTasks() {
        return tasksMap.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epicsMap.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return this.getSubtasksMap().values().stream().toList();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();
        Subtask subtask = subtasksMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return epicsMap.get(epicId).getSubtasks();
    }

    @Override
    public void clearTasks() {
        for (Integer id : tasksMap.keySet()) {
            historyManager.remove(id);
        }
        tasksMap.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer id : epicsMap.keySet()) {
            historyManager.remove(id);
        }
        epicsMap.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epicsMap.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
            }

            epic.clearSubtasks();
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            return;
        }

        tasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            return;
        }

        for (Subtask subtask : epicsMap.get(id).getSubtasks()) {
            historyManager.remove(subtask.getId());
        }

        epicsMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();

        if (!subtasksMap.containsKey(id)) {
            return;
        }

        int epicId = subtasksMap.get(id).getEpicId();
        epicsMap.get(epicId).deleteSubtask(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!epicsMap.containsKey(subtask.getEpicId())) {
            return;
        }

        epicsMap.get(subtask.getEpicId()).updateSubtask(subtask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

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

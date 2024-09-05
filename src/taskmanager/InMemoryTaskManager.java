package taskmanager;

import exception.ObjectNotFound;
import exception.TasksOverlappedException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasksMap;
    protected final Map<Integer, Epic> epicsMap;
    protected Set<Task> sortedTasks;
    private final HistoryManager historyManager;
    private int taskId;

    public InMemoryTaskManager() {
        taskId = 1;
        tasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();

        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void createTask(Task task) {
        if (task == null) {
            return;
        }

        if (isTasksOverlapped(task)) {
            throw new TasksOverlappedException("Задача " + task.getTaskDescription() +
                    " не была добавлена из-за пересечения во времени с другими задачами");
        }

        int newId = getAndIncrementId();
        task.setId(newId);
        tasksMap.put(newId, task);

        addTaskToSortedList(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        int newId = getAndIncrementId();
        epic.setId(newId);
        epic.updateEpicStatus();
        epicsMap.put(newId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask == null || epicsMap.get(subtask.getEpicId()) == null) {
            return;
        }

        if (isTasksOverlapped(subtask)) {
            throw new TasksOverlappedException("Задача " + subtask.getTaskDescription() +
                    " не была добавлена из-за пересечения во времени с другими задачами");
        }

        int newId = getAndIncrementId();
        subtask.setId(newId);
        epicsMap.get(subtask.getEpicId()).addSubtask(newId, subtask);

        addTaskToSortedList(subtask);
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
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            throw new ObjectNotFound("Задача не найдена");
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            throw new ObjectNotFound("Эпик не найден");
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();
        Subtask subtask = subtasksMap.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        } else {
            throw new ObjectNotFound("Подзадача не найдена");
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return epicsMap.get(epicId).getSubtasks();
    }

    @Override
    public boolean isTaskExists(int id) {
        return tasksMap.containsKey(id);
    }

    @Override
    public boolean isEpicExists(int id) {
        return epicsMap.containsKey(id);
    }

    @Override
    public boolean isSubtaskExists(int id) {
        return this.getSubtasksMap().containsKey(id);
    }

    @Override
    public void clearTasks() {
        tasksMap.keySet().forEach(
                key -> {
                    historyManager.remove(key);
                    if (tasksMap.get(key).getStartTime() != null) {
                        sortedTasks.remove(tasksMap.get(key));
                    }
                }

        );
        tasksMap.clear();
    }

    @Override
    public void clearEpics() {
        epicsMap.keySet().forEach(historyManager::remove);
        epicsMap.clear();
    }

    @Override
    public void clearSubtasks() {
        epicsMap.values().stream()
                .filter(epic -> !epic.isEmpty())
                .forEach(epic -> {
                    epic.getSubtasks().forEach(subtask -> {
                        historyManager.remove(subtask.getId());
                        if (subtask.getStartTime() != null) {
                            sortedTasks.remove(subtask);
                        }
                    });
                    epic.clearSubtasks();
                });
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            return;
        }

        if (tasksMap.get(id).getStartTime() != null) {
            sortedTasks.remove(tasksMap.get(id));
        }
        tasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            return;
        }

        epicsMap.get(id).getSubtasks().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                sortedTasks.remove(subtask);
            }
        });

        epicsMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Map<Integer, Subtask> subtasksMap = getSubtasksMap();

        if (!subtasksMap.containsKey(id)) {
            return;
        }

        if (getSubtaskById(id).getStartTime() != null) {
            sortedTasks.remove(getSubtaskById(id));
        }

        int epicId = subtasksMap.get(id).getEpicId();
        epicsMap.get(epicId).deleteSubtask(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasksMap.containsKey(task.getId())) {

            if (isTasksOverlapped(task)) {
                throw new TasksOverlappedException("Задача " + task.getTaskDescription() +
                        " не была добавлена из-за пересечения во времени с другими задачами");
            }

            if (tasksMap.get(task.getId()).getStartTime() != null) {
                sortedTasks.remove(tasksMap.get(task.getId()));
            }
            tasksMap.put(task.getId(), task);

            addTaskToSortedList(task);
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

        if (isTasksOverlapped(subtask)) {
            throw new TasksOverlappedException("Задача " + subtask.getTaskDescription() +
                    " не была добавлена из-за пересечения во времени с другими задачами");
        }

        if (getSubtaskById(subtask.getId()).getStartTime() != null) {
            sortedTasks.remove(getSubtaskById(subtask.getId()));
        }
        epicsMap.get(subtask.getEpicId()).updateSubtask(subtask);

        addTaskToSortedList(subtask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public boolean isTasksOverlapped(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(task -> !task.equals(newTask))
                .anyMatch(task -> isPeriodsOverlapped(newTask.getStartTime(), newTask.getEndTime(),
                        task.getStartTime(), task.getEndTime()));

    }

    public void addTaskToSortedList(Task task) {
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    protected void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    private boolean isPeriodsOverlapped(LocalDateTime startDate1, LocalDateTime endDate1,
                                        LocalDateTime startDate2, LocalDateTime endDate2) {
        return !startDate1.isAfter(endDate2) && !endDate1.isBefore(startDate2);
    }

    private int getAndIncrementId() {
        return taskId++;
    }

    private Map<Integer, Subtask> getSubtasksMap() {
        return epicsMap.values().stream()
                .filter(epic -> !epic.isEmpty())
                .flatMap(epic -> epic.getSubtasks().stream())
                .collect(Collectors.toMap(Task::getId, subtask -> subtask));
    }
}

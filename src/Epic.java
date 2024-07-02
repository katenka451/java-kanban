import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtasksMap;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        subtasksMap = new HashMap<>();
    }

    public Epic(Epic epic) {
        super(epic.getTaskName(), epic.getTaskDescription());
        setTaskStatus(epic.getTaskStatus());
        subtasksMap = new HashMap<>();
        epic.subtasksMap.forEach((id, subtask) -> {
            subtasksMap.put(id, new Subtask(subtask));
        });
    }

    public void addSubtask(int id, Subtask subtask) {
        if (!subtasksMap.containsKey(id)) {
            subtasksMap.put(id, subtask);
        }
        updateEpicStatus();
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
        }
        updateEpicStatus();
    }

    public List<Subtask> getSubtasks() {
        return subtasksMap.values().stream().toList();
    }

    public void deleteSubtask(int id) {
        subtasksMap.remove(id);
        updateEpicStatus();
    }

    public void clearSubtasks() {
        subtasksMap.clear();
        updateEpicStatus();
    }

    public boolean hasSubtask(int id) {
        return subtasksMap.containsKey(id);
    }

    @Override
    public void setTaskStatus(Status taskStatus) {
        throw new RuntimeException("Epic status cannot be changed manually");
    }

    private void updateEpicStatus() {
        int subtaskNew = 0;
        int subtaskDone = 0;
        for (Subtask subtask : subtasksMap.values()) {
            switch (subtask.getTaskStatus()) {
                case NEW -> subtaskNew++;
                case DONE -> subtaskDone++;
            }
        }
        if (subtaskNew == subtasksMap.size()) {
            this.taskStatus = Status.NEW;
        } else if (subtaskDone == subtasksMap.size()) {
            this.taskStatus = Status.DONE;
        } else {
            this.taskStatus = Status.IN_PROGRESS;
        }
    }
}

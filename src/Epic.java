import java.util.HashMap;
import java.util.List;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtasksMap;

    public Epic(String taskName, String taskDescription, int id) {
        super(taskName, taskDescription, id);
        subtasksMap = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        if (!subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
        }
    }

    public List<Subtask> getSubtasks() {
        return subtasksMap.values().stream().toList();
    }

    public void deleteSubtask(int id) {
        subtasksMap.remove(id);
    }

    public void clearSubtasks() {
        subtasksMap.clear();
    }

    public boolean hasSubtask(int id) {
        return subtasksMap.containsKey(id);
    }

    @Override
    public Status getTaskStatus() {
        int subtaskNew = 0;
        int subtaskDone = 0;
        for (Subtask subtask : subtasksMap.values()) {
            switch (subtask.getTaskStatus()) {
                case NEW -> subtaskNew++;
                case DONE -> subtaskDone++;
            }
        }
        if (subtaskNew == subtasksMap.size()) {
            return Status.NEW;
        } else if (subtaskDone == subtasksMap.size()) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public void setTaskStatus(Status taskStatus) {
        throw new RuntimeException("Epic status cannot be changed manually");
    }
}

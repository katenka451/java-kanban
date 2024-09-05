package model;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String taskName, String taskDescription, int epicId) {
        super(taskName, taskDescription);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getTaskName(), subtask.getTaskDescription());
        setTaskStatus(subtask.getTaskStatus());
        this.id = subtask.getId();
        this.epicId = subtask.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }
}

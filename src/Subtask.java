public class Subtask extends Task {

    private final int epicId;

    public Subtask(String taskName, String taskDescription, int id, int epicId) {
        super(taskName, taskDescription, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}

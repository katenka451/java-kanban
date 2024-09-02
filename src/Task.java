import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    protected final String taskName;
    protected final String taskDescription;
    protected int id;
    protected Status taskStatus;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = Status.NEW;
    }

    public Task(Task task) {
        this.taskName = task.getTaskName();
        this.taskDescription = task.getTaskDescription();
        this.taskStatus = task.getTaskStatus();
        this.id = task.getId();
        this.duration = task.getDuration() != null ? Duration.ofMinutes(task.getDuration().toMinutes()) : null;
        this.startTime = task.getStartTime() != null ? LocalDateTime.from(task.getStartTime()) : null;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (this.startTime == null || this.duration == null) {
            return null;
        }
        return this.startTime.plusMinutes(this.duration.toMinutes());
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}

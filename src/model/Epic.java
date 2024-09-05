package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtasksMap;

    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        subtasksMap = new HashMap<>();
    }

    public Epic(Epic epic) {
        super(epic.getTaskName(), epic.getTaskDescription());
        this.taskStatus = epic.getTaskStatus();
        this.id = epic.getId();
        subtasksMap = new HashMap<>();
        epic.subtasksMap.forEach((id, subtask) -> subtasksMap.put(id, new Subtask(subtask)));
        this.duration = epic.getDuration() != null ? Duration.ofMinutes(epic.getDuration().toMinutes()) : null;
        this.startTime = epic.getStartTime() != null ? LocalDateTime.from(epic.getStartTime()) : null;
        this.endTime = epic.getStartTime() != null ? LocalDateTime.from(epic.getEndTime()) : null;
    }

    public void addSubtask(int id, Subtask subtask) {
        if (!subtasksMap.containsKey(id)) {
            subtasksMap.put(id, subtask);
        }
        updateEpicStatus();
        updateDatesAndDuration();
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
        }
        updateEpicStatus();
        updateDatesAndDuration();
    }

    public List<Subtask> getSubtasks() {
        return subtasksMap.values().stream().toList();
    }

    public void deleteSubtask(int id) {
        subtasksMap.remove(id);
        updateEpicStatus();
        updateDatesAndDuration();
    }

    public void clearSubtasks() {
        subtasksMap.clear();
        updateEpicStatus();
        this.startTime = null;
        this.duration = null;
        this.endTime = null;
    }

    public boolean hasSubtask(int id) {
        return subtasksMap.containsKey(id);
    }

    public boolean isEmpty() {
        return subtasksMap.isEmpty();
    }

    @Override
    public void setTaskStatus(Status taskStatus) {
        throw new RuntimeException("model.Epic status cannot be changed manually");
    }

    @Override
    public void setDuration(Duration duration) {
        throw new RuntimeException("model.Epic duration cannot be changed manually");
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        throw new RuntimeException("model.Epic start time cannot be changed manually");
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateEpicStatus() {
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

    private void updateDatesAndDuration() {
        this.startTime = subtasksMap.values().stream()
                .filter(subtask -> subtask.startTime != null)
                .min(Comparator.comparing(subtask -> subtask.startTime))
                .map(subtask -> subtask.startTime)
                .orElse(null);

        this.duration = Duration.ofMinutes(
                subtasksMap.values().stream()
                        .filter(subtask -> subtask.duration != null)
                        .mapToLong(subtask -> subtask.duration.toMinutes())
                        .sum()
        );

        this.endTime = subtasksMap.values().stream()
                .filter(subtask -> subtask.startTime != null)
                .max(Comparator.comparing(Task::getEndTime))
                .map(Task::getEndTime)
                .orElse(null);
    }

}

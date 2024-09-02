import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HistoryManagerTest {

    @ParameterizedTest
    @MethodSource("provideParameters")
    void fullTestScenario(HistoryManager historyManager) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        task1.setStartTime(LocalDateTime.parse("2024-07-15T21:21:21"));
        task1.setDuration(Duration.ofDays(3));
        taskManager.createTask(task1);

        Task task2 = new Task(
                "Задача 2",
                "Тестовая задача 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1 для эпика 1",
                epic1.getId());
        subtask1ForEpic1.setStartTime(LocalDateTime.parse("2024-05-22T21:21:21"));
        subtask1ForEpic1.setDuration(Duration.ofDays(3));
        taskManager.createSubtask(subtask1ForEpic1);

        Subtask subtask2ForEpic1 = new Subtask(
                "Подзадача 1.2",
                "Тестовая подзадача 2 для эпика 1",
                epic1.getId());
        subtask2ForEpic1.setStartTime(LocalDateTime.parse("2024-08-22T21:21:21"));
        subtask2ForEpic1.setDuration(Duration.ofDays(1));
        taskManager.createSubtask(subtask2ForEpic1);

        Subtask subtask3ForEpic1 = new Subtask(
                "Подзадача 1.3",
                "Тестовая подзадача 3 для эпика 1",
                epic1.getId());
        subtask3ForEpic1.setTaskStatus(Status.DONE);
        subtask3ForEpic1.setStartTime(LocalDateTime.parse("2024-08-15T21:21:21"));
        subtask3ForEpic1.setDuration(Duration.ofDays(5));
        taskManager.createSubtask(subtask3ForEpic1);

        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task1);
        historyManager.add(subtask1ForEpic1);
        historyManager.add(epic1);
        historyManager.add(subtask2ForEpic1);
        historyManager.add(subtask1ForEpic1);
        historyManager.add(task2);

        assertEquals(historyManager.getHistory().getLast().getId(), task1.getId(),
                    "Некорректный последний элемент");

        assertEquals(historyManager.getHistory().getFirst().getId(), task2.getId(),
                "Некорректный первый элемент");

        Set<Task> duplicates = new HashSet<>();
        boolean duplicate = historyManager.getHistory().stream()
                .anyMatch(task -> !duplicates.add(task));

        assertFalse(duplicate, "В списке присутствуют дубликаты");
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(Arguments.of(Managers.getDefaultHistory()));
    }
}

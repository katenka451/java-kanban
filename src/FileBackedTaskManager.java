import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path tasksFile;
    public static final String CSV_HEADER = "id,type,name,status,description,epic";
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TYPE = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_STATUS = 3;
    private static final int COLUMN_DESCRIPTION = 4;
    private static final int COLUMN_EPIC = 5;

    public FileBackedTaskManager(Path tasksFile) {
        super();
        this.tasksFile = tasksFile;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    public Task fromString(String value) {
        String[] splitRow = value.split(",");

        TasksTypes type = TasksTypes.valueOf(splitRow[COLUMN_TYPE]);
        switch (type) {
            case TasksTypes.EPIC:
                if (!isEpicExists(Integer.parseInt(splitRow[COLUMN_ID]))) {
                    Epic epic = new Epic(
                            splitRow[COLUMN_NAME],
                            splitRow[COLUMN_DESCRIPTION]);
                    epic.setId(Integer.parseInt(splitRow[COLUMN_ID]));
                    return epic;
                }
                break;
            case TasksTypes.SUBTASK:
                if (!isSubtaskExists(Integer.parseInt(splitRow[COLUMN_ID]))) {
                    //Предположим (согласно логике в save(), что в файле всегда сначала идет эпик,
                    // а потом его подзадачи, по порядку
                    // Более сложные проверки по ТЗ пока не требуются
                    if (isEpicExists(Integer.parseInt(splitRow[COLUMN_EPIC]))) {
                        Subtask subtask = new Subtask(
                                splitRow[COLUMN_NAME],
                                splitRow[COLUMN_DESCRIPTION],
                                Integer.parseInt(splitRow[COLUMN_EPIC]));
                        subtask.setId(Integer.parseInt(splitRow[COLUMN_ID]));
                        subtask.setTaskStatus(Status.valueOf(splitRow[COLUMN_STATUS]));
                        return subtask;
                    }
                }
                break;
            case TasksTypes.TASK:
                if (!isTaskExists(Integer.parseInt(splitRow[COLUMN_ID]))) {
                    Task task = new Task(
                            splitRow[COLUMN_NAME],
                            splitRow[COLUMN_DESCRIPTION]);
                    task.setId(Integer.parseInt(splitRow[COLUMN_ID]));
                    task.setTaskStatus(Status.valueOf(splitRow[COLUMN_STATUS]));
                    return task;
                }
                break;
            default:
                return null;
        }
        return null;
    }

    private void save() {
        try (FileWriter fileWriter =
                     new FileWriter(this.tasksFile.toString(), StandardCharsets.UTF_8)) {
            //Если save без параметров, то получается, что мы каждый раз перезаписываем файл
            fileWriter.append(CSV_HEADER).append(System.lineSeparator());

            for (Task task : getTasks()) {
                fileWriter.append(toString(task));
            }

            for (Epic epic : getEpics()) {
                fileWriter.append(toString(epic));
                for (Subtask subtask : getSubtasksOfEpic(epic.getId())) {
                    fileWriter.append(toString(subtask));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных");
        }
    }

    private String toString(Task task) {
        String taskInString;
        switch (task) {
            case Epic epic -> taskInString =
                    String.format("%d,%s,%s,%s,%s" + System.lineSeparator(),
                            epic.getId(),
                            TasksTypes.EPIC,
                            epic.getTaskName(),
                            epic.getTaskStatus(),
                            epic.getTaskDescription());
            case Subtask subtask -> taskInString =
                    String.format("%d,%s,%s,%s,%s,%d" + System.lineSeparator(),
                            subtask.getId(),
                            TasksTypes.SUBTASK,
                            subtask.getTaskName(),
                            subtask.getTaskStatus(),
                            subtask.getTaskDescription(),
                            subtask.getEpicId());
            default -> taskInString =
                    String.format("%d,%s,%s,%s,%s" + System.lineSeparator(),
                            task.getId(),
                            TasksTypes.TASK,
                            task.getTaskName(),
                            task.getTaskStatus(),
                            task.getTaskDescription());
        }
        return taskInString;
    }

    public static FileBackedTaskManager loadFromFile(Path tasksFile) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tasksFile);

        String content;
        try {
            content = Files.readString(tasksFile);
        } catch (IOException ex) {
            throw new ManagerReadException("Ошибка чтения данных");
        }

        int maxId = 0;
        String[] splitContent = content.split(System.lineSeparator());

        //Начинаем со второй строки, т.к. первая - это заголовок
        for (int i = 1; i < splitContent.length; i++) {
            Task task = taskManager.fromString(splitContent[i]);
            switch (task) {
                case Epic epic -> taskManager.epicsMap.put(epic.getId(), epic);
                case Subtask subtask -> taskManager.epicsMap.get(subtask.getEpicId()).addSubtask(subtask.getId(), subtask);
                default -> taskManager.tasksMap.put(task.getId(), task);
            }
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        //Обновляем счетчик ID
        taskManager.setTaskId(maxId + 1);

        return taskManager;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите путь к файлу/директории: ");
        String enteredPath = scanner.nextLine();
        Path tasksFile = Paths.get(enteredPath);

        if (!Files.exists(tasksFile)) {
            System.out.println("Введённый путь не существует.");
            return;
        }

        TaskManager manager = loadFromFile(tasksFile);

        Task task1 = new Task(
                "Задача 1",
                "Тестовая задача 1");
        manager.createTask(task1);

        Task task2 = new Task(
                "Задача 2",
                "Тестовая задача 2");
        manager.createTask(task2);

        Epic epic1 = new Epic(
                "Эпик 1",
                "Тестовый эпик 1");
        manager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(
                "Подзадача 1.1",
                "Тестовая подзадача 1 для эпика 1",
                epic1.getId());
        manager.createSubtask(subtask1ForEpic1);
        Subtask subtask2ForEpic1 = new Subtask(
                "Подзадача 1.2",
                "Тестовая подзадача 2 для эпика 1",
                epic1.getId());
        manager.createSubtask(subtask2ForEpic1);
        Subtask subtask3ForEpic1 = new Subtask(
                "Подзадача 1.3",
                "Тестовая подзадача 3 для эпика 1",
                epic1.getId());
        subtask3ForEpic1.setTaskStatus(Status.IN_PROGRESS);
        manager.createSubtask(subtask3ForEpic1);

        Epic epic2 = new Epic(
                "Эпик 2",
                "Тестовый эпик 2");
        manager.createEpic(epic2);
    }
}

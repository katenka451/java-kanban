
public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(
                "Task1",
                "Test task1");
        manager.createTask(task1);

        Task task2 = new Task(
                "Task2",
                "Test task2");
        manager.createTask(task2);

        Epic epic1 = new Epic(
                "Epic1",
                "Test epic1");
        manager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask(
                "Subtask 1.1",
                "Test subtask 1.1",
                epic1.getId());
        manager.createSubtask(subtask1_1);
        Subtask subtask1_2 = new Subtask(
                "Subtask 1.2",
                "Test subtask 1.2",
                epic1.getId());
        manager.createSubtask(subtask1_2);
        Subtask subtask1_3 = new Subtask(
                "Subtask 1.3",
                "Test subtask 1.3",
                epic1.getId());
        manager.createSubtask(subtask1_3);

        Epic epic2 = new Epic(
                "Epic2",
                "Test epic2");
        manager.createEpic(epic2);

        Task findEpic1 = manager.getEpicById(epic1.getId());
        Task findTask2 = manager.getTaskById(task2.getId());
        Task findSubtask1_1 = manager.getSubtaskById(subtask1_1.getId());
        Task findEpic2 = manager.getEpicById(epic2.getId());
        findEpic2 = manager.getEpicById(epic2.getId());
        Task findSubtask1_3 = manager.getSubtaskById(subtask1_3.getId());
        findEpic2 = manager.getEpicById(epic2.getId());
        Task findSubtask1_2 = manager.getSubtaskById(subtask1_2.getId());
        findEpic2 = manager.getEpicById(epic2.getId());
        findEpic2 = manager.getEpicById(epic2.getId());
        Task findTask1 = manager.getTaskById(task1.getId());
        findTask1 = manager.getTaskById(task1.getId());
        findEpic2 = manager.getEpicById(epic2.getId());
        findSubtask1_3 = manager.getSubtaskById(subtask1_3.getId());
        findTask2 = manager.getTaskById(task2.getId());
        printHistory(manager);

        manager.deleteSubtaskById(subtask1_3.getId());
        printHistory(manager);

        manager.deleteEpicById(epic1.getId());
        printHistory(manager);

    }

    static void printAllTasks(TaskManager manager) {
        System.out.println("Список всех задач");
        for (Task task : manager.getTasks()) {
            System.out.println("Задача № " + task.getId() + ": " + task.getTaskName() +
                    " (" + task.getTaskDescription() + "), статус - " + task.getTaskStatus());
        }
        System.out.println("------------------");
        System.out.println("Список всех эпиков");
        for (Epic epic : manager.getEpics()) {
            System.out.println("Эпик № " + epic.getId() + ": " + epic.getTaskName() +
                    " (" + epic.getTaskDescription() + "), статус - " + epic.getTaskStatus());
            for (Subtask subtask : epic.getSubtasks()) {
                System.out.println("Подзадача № " + subtask.getId() + ": " + subtask.getTaskName() +
                        " (" + subtask.getTaskDescription() + "), статус - " + subtask.getTaskStatus());
            }
        }
        System.out.println("------------------");
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task.getTaskName());
        }
        System.out.println("------------------");
    }

    static void printHistory(TaskManager manager) {
        System.out.println("------------------");
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task.getTaskName());
        }
        System.out.println("------------------");
    }
}

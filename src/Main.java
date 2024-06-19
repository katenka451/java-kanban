public class Main {

    public static void main(String[] args) {
        //По ТЗ неясно, нужно ли оставлять код для тестирования. Решила оставить
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(
                "Task1",
                "Test task1",
                taskManager.getAndIncrementId());
        taskManager.createTask(task1);

        Task task2 = new Task(
                "Task2",
                "Test task2",
                taskManager.getAndIncrementId());
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Epic1",
                "Test epic1",
                taskManager.getAndIncrementId());
        taskManager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask(
                "Subtask 1.1",
                "Test subtask 1.1",
                taskManager.getAndIncrementId(),
                epic1.getId());
        taskManager.createSubtask(subtask1_1);
        Subtask subtask1_2 = new Subtask(
                "Subtask 1.2",
                "Test subtask 1.2",
                taskManager.getAndIncrementId(),
                epic1.getId());
        taskManager.createSubtask(subtask1_2);

        Epic epic2 = new Epic(
                "Epic2",
                "Test epic2",
                taskManager.getAndIncrementId());
        taskManager.createEpic(epic2);
        Subtask subtask2_1 = new Subtask(
                "Subtask 2.1",
                "Test subtask 2.1",
                taskManager.getAndIncrementId(),
                epic2.getId());
        taskManager.createSubtask(subtask2_1);

        printAllTasks(taskManager);

        task1.setTaskStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1_1.setTaskStatus(Status.DONE);
        taskManager.updateSubtask(subtask1_1);

        System.out.println("------------------");
        printAllTasks(taskManager);

        taskManager.deleteSubtaskById(subtask1_1.getId());

        System.out.println("------------------");
        printAllTasks(taskManager);

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println("------------------");
        printAllTasks(taskManager);

    }

    static void printAllTasks(TaskManager taskManager) {
        System.out.println("Список всех задач");
        for (Task task : taskManager.getTasksMap().values()) {
            System.out.println("Задача № " + task.getId() + ": " + task.getTaskName() +
                    " (" + task.getTaskDescription() + "), статус - " + task.getTaskStatus());
        }

        System.out.println("Список всех эпиков");
        for (Epic epic : taskManager.getEpicsMap().values()) {
            System.out.println("Эпик № " + epic.getId() + ": " + epic.getTaskName() +
                    " (" + epic.getTaskDescription() + "), статус - " + epic.getTaskStatus());
            for (Subtask subtask : epic.getSubtasks()) {
                System.out.println("Подзадача № " + subtask.getId() + ": " + subtask.getTaskName() +
                        " (" + subtask.getTaskDescription() + "), статус - " + subtask.getTaskStatus());
            }
        }
    }
}

package taskmanager;

import java.nio.file.Paths;

public class Managers {

    private static final String FILE_PATH = "kanban_backup.csv";

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedManager() {
        return FileBackedTaskManager.loadFromFile(Paths.get(FILE_PATH));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}

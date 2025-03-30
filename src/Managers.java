import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTM(File file) {
        return new FileBackedTaskManager(file);
    }

    public static InMemoryHistoryManager getInMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }
}

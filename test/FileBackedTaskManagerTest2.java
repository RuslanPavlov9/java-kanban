import exception.ManagerSaveException;
import org.junit.jupiter.api.Test;
import taskmanagers.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest2 extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @Test
    public void shouldSaveAndLoadEmptyTasks() {
        FileBackedTaskManager manager1 = createTaskManager();
        manager1.save();

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(0, manager2.getTasks().size(), "Список задач должен быть пустым");
        assertEquals(0, manager2.getEpicsTasks().size(), "Список эпиков должен быть пустым");
        assertEquals(0, manager2.getSubTasks().size(), "Список подзадач должен быть пустым");
    }

    @Test
    public void shouldThrowExceptionWhenFileNotFound() {
        File notExistingFile = new File("not_existing_file.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(notExistingFile),
                "Должно быть исключение при загрузке из несуществующего файла");
    }
}
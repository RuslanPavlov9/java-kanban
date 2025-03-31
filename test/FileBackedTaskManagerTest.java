import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), new byte[0]);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpicsTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());
    }

    @Test
    void shouldSaveMultipleTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        EpicTask epic1 = new EpicTask(2, "Эпик 1", "Описание эпика 1", Status.NEW);
        SubTask subTask1 = new SubTask(3, "Подзадача 1", "Описание подзадачи 1", Status.NEW, 2);

        manager.addTask(task1);
        manager.addEpicTask(epic1);
        manager.addSubTask(subTask1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getTasks();
        List<EpicTask> epics = loadedManager.getEpicsTasks();
        List<SubTask> subTasks = loadedManager.getSubTasks();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subTasks.size());

        assertEquals("Задача 1", tasks.get(0).getTitle());
        assertEquals("Эпик 1", epics.get(0).getTitle());
        assertEquals("Подзадача 1", subTasks.get(0).getTitle());
    }

    @Test
    void shouldLoadMultipleTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        EpicTask epic1 = new EpicTask(3, "Эпик 1", "Описание эпика 1", Status.DONE);
        SubTask subTask1 = new SubTask(4, "Подзадача 1", "Описание подзадачи 1", Status.NEW, 3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpicTask(epic1);
        manager.addSubTask(subTask1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpicsTasks().size());
        assertEquals(1, loadedManager.getSubTasks().size());

        Task loadedTask = loadedManager.getTasks().get(0);
        assertEquals("Задача 1", loadedTask.getTitle());
        assertEquals(Status.NEW, loadedTask.getStatus());

        SubTask loadedSubTask = loadedManager.getSubTasks().get(0);
        assertEquals(3, loadedSubTask.getEpicId());
    }

}
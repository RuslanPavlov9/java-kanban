import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagers.FileBackedTaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

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
        Files.write(tempFile.toPath(), "id,type,name,status,description,duration,startTime,epicId\n".getBytes());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpicsTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTaskWithTimeFields() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);
        Task task = new Task(1, "Задача", "Описание", Status.NEW, duration, startTime);

        manager.addTask(task);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = loadedManager.getTaskById(1);
        assertEquals(duration, loadedTask.getDuration());
        assertEquals(startTime, loadedTask.getStartTime());
    }

    @Test
    void shouldSaveAndLoadEpicWithSubTasks() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(1);

        EpicTask epic = new EpicTask(1, "Эпик", "Описание", Status.NEW);
        SubTask subTask = new SubTask(2, "Подзадача", "Описание", Status.NEW,
                1, duration, startTime);

        manager.addEpicTask(epic);
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        EpicTask loadedEpic = loadedManager.getEpicTaskById(1);
        SubTask loadedSubTask = loadedManager.getSubTaskById(2);

        assertEquals(duration, loadedSubTask.getDuration());
        assertEquals(startTime, loadedSubTask.getStartTime());
        assertEquals(startTime, loadedEpic.getStartTime());
        assertEquals(duration, loadedEpic.getDuration());
    }

    @Test
    void shouldHandleNullTimeFields() {
        Task task = new Task(1, "Task", "Описание", Status.NEW);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskById(1);

        assertNull(loadedTask.getDuration());
        assertNull(loadedTask.getStartTime());
    }

    @Test
    void shouldSaveAndLoadAllFieldsInCorrectOrder() throws IOException {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(1);
        Task task = new Task(1, "Задача", "Описание", Status.NEW, duration, startTime);
        manager.addTask(task);

        String fileContent = Files.readString(tempFile.toPath());
        String[] lines = fileContent.split("\n");
        String taskLine = lines[1];

        String[] fields = taskLine.split(",");
        assertEquals("1", fields[0]); // айди
        assertEquals("TASK", fields[1]); // тип
        assertEquals("Задача", fields[2]); // тайтл
        assertEquals("NEW", fields[3]); // статус
        assertEquals("Описание", fields[4]); // описание
        assertEquals("60", fields[5]); // продолжительность
        assertEquals(startTime.toString(), fields[6]); // время начала
    }

    @Test
    void shouldUpdateEpicTimeFieldsWhenSubTaskRemoved() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 1, 1, 12, 0);
        Duration duration = Duration.ofHours(1);

        EpicTask epic = new EpicTask(1, "Эпик", "Описание", Status.NEW);
        SubTask subTask1 = new SubTask(2, "Подзадача 1", "Описание", Status.NEW,
                1, duration, startTime1);
        SubTask subTask2 = new SubTask(3, "Подзадача 2", "Описание", Status.NEW,
                1, duration, startTime2);

        manager.addEpicTask(epic);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        manager.removeSubTaskById(2);

        EpicTask updatedEpic = manager.getEpicTaskById(1);
        assertEquals(startTime2, updatedEpic.getStartTime());
        assertEquals(duration, updatedEpic.getDuration());
    }
}
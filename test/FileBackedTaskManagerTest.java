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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File file;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks1", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        loadedManager.loadFromFile();

        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpicsTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task = new Task(1, "Task1", "Description task1", Status.NEW);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        loadedManager.loadFromFile();

        List<Task> tasks = loadedManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void testSaveAndLoadEpics() {
        EpicTask epic = new EpicTask(2, "Epic2", "Description epic2", Status.DONE);
        manager.addEpicTask(epic);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        loadedManager.loadFromFile();

        List<EpicTask> epics = loadedManager.getEpicsTasks();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

//    @Test
//    void testSaveAndLoadSubTasks() {
//        EpicTask epic = new EpicTask(2, "Epic2", "Description epic2", Status.DONE);
//        manager.addEpicTask(epic);
//
//        SubTask subTask = new SubTask(3, "Sub Task2", "Description sub task3", Status.DONE, 2);
//        manager.addSubTask(subTask);
//
//        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
//        loadedManager.loadFromFile();
//
//        List<SubTask> subTasks = loadedManager.getSubTasks();
//        assertEquals(1, subTasks.size());
//        assertEquals(subTask, subTasks.get(0));
//
//        List<EpicTask> epics = loadedManager.getEpicsTasks();
//        assertEquals(1, epics.size());
//        assertTrue(epics.get(0).getSubTaskIds().contains(subTask.getId()));
//    }

    @Test
    void testSaveAndLoadAllTypes() {
        Task task = new Task(1, "Task1", "Description task1", Status.NEW);
        EpicTask epic = new EpicTask(2, "Epic2", "Description epic2", Status.DONE);
        SubTask subTask = new SubTask(3, "Sub Task2", "Description sub task3", Status.DONE, 2);

        manager.addTask(task);
        manager.addEpicTask(epic);
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        loadedManager.loadFromFile();

        List<Task> tasks = loadedManager.getTasks();
        List<EpicTask> epics = loadedManager.getEpicsTasks();
        List<SubTask> subTasks = loadedManager.getSubTasks();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subTasks.size());

        assertEquals(task, tasks.get(0));
        assertEquals(epic, epics.get(0));
        assertEquals(subTask, subTasks.get(0));
        assertTrue(epics.get(0).getSubTaskIds().contains(subTask.getId()));
    }

    @Test
    void testLoadFromFileWithInvalidData() throws IOException {
        // Создаем файл с некорректными данными
        Files.writeString(file.toPath(), "id,type,name,status,description,epicId\n1,INVALID_TYPE,Task1,NEW,Description task1,");

        assertThrows(IllegalArgumentException.class, () -> manager.loadFromFile());
    }

}
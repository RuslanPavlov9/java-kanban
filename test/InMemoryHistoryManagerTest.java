import enums.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    private static TaskManager tm;
    private Task task1, task2, task3;

    @BeforeEach
    public void setUp() {
        tm = Managers.getDefault();

        task1 = new Task("1", "Task 1");
        task2 = new Task("2", "Task 2");
        task3 = new Task("3", "Task 3");
    }

    @Test
    public void getHistoryShouldReturnListOfTasks() {
        Task task = new Task("задача типовая", "описание");
        tm.addTask(task);
        int taskId = task.getId();
        tm.getTaskById(taskId);
        List<Task> list = tm.getHistory();
        assertEquals(1, list.size(), "Ожидался список из одной задачи");
    }

    @Test
    public void shoulldReturnEqualsFields() {
        Task task1 = new Task("задача типовая", "описание", 1, Status.NEW);
        tm.addTask(task1);
        int taskId = task1.getId();
        Task taskFromTm = tm.getTaskById(taskId);
        assertEquals(1, taskFromTm.getId());
        assertEquals("задача типовая", taskFromTm.getTitle());
        assertEquals("описание", taskFromTm.getDescription());
        assertEquals(Status.NEW, taskFromTm.getStatus());

    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task task1 = new Task("Заголовок1", "Описание1");
        tm.addTask(task1);
        tm.getTaskById(task1.getId());
        tm.updateTask(new Task("Заголовок 2", "Описание 2", task1.getId(), Status.IN_PROGRESS));
        List<Task> tasks = tm.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(task1.getTitle(), oldTask.getTitle(), "старой версии задачи нет в истории");
        assertEquals(task1.getDescription(), oldTask.getDescription(),
                "старой версии задачи нет в истории");

    }

    @Test
    public void getIdTmShouldReturnOldTaskAfterDelete() {
        Task task1 = new Task("Заголовок1", "Описание1");
        Task task2 = new Task("Заголовок1", "Описание1", 1, Status.IN_PROGRESS);
        tm.addTask(task1);
        int taskId1 = task1.getId();
        tm.addTask(task2);
        int taskId2 = task2.getId();
        assertFalse(taskId1 == taskId2, "Идентификаторы задач совпадают");
    }

    @Test
    public void testAdd_singleTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);
        List<Task> history = tm.getHistory();
        assertEquals(List.of(task1), history, "История должна содержать одну задачу");
    }

    @Test
    public void testAdd_multipleTasks() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);
        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);
        tm.addTask(task3);
        taskId = task3.getId();
        tm.getTaskById(taskId);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task1, task2, task3), history, "История должна содержать задачи в порядке обращения");
    }

    @Test
    public void testAdd_duplicateTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        taskId = task1.getId();
        tm.getTaskById(taskId);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task2, task1), history, "Дубликат должен быть перемещен в конец");
    }

    @Test
    public void testRemove_existingTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        tm.addTask(task3);
        taskId = task3.getId();
        tm.getTaskById(taskId);

        tm.removeTaskById(2); // Удаляем задачу с id=2

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task1, task3), history, "Задача с id=2 должна быть удалена");
    }

    @Test
    public void testRemove_nonExistingTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        tm.removeTaskById(5);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task1, task2), history, "Удаление несуществующей задачи не должно влиять на историю");
    }

    @Test
    public void testRemove_headTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        tm.addTask(task3);
        taskId = task3.getId();
        tm.getTaskById(taskId);

        tm.removeTaskById(1);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task2, task3), history, "Голова должна быть удалена");
    }

    @Test
    public void testRemove_tailTask() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        tm.addTask(task3);
        taskId = task3.getId();
        tm.getTaskById(taskId);

        tm.removeTaskById(3);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task1, task2), history, "Хвост должен быть удален");
    }

    @Test
    public void testGetHistory_emptyHistory() {
        List<Task> history = tm.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testAdd_afterRemove() {
        tm.addTask(task1);
        int taskId = task1.getId();
        tm.getTaskById(taskId);

        tm.addTask(task2);
        taskId = task2.getId();
        tm.getTaskById(taskId);

        tm.removeTaskById(1);

        tm.addTask(task3);
        taskId = task3.getId();
        tm.getTaskById(taskId);

        List<Task> history = tm.getHistory();
        assertEquals(List.of(task2, task3), history, "После удаления и добавления история должна быть корректной");
    }

}

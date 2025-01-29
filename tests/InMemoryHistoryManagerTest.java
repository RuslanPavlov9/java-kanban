import enums.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InMemoryHistoryManagerTest {
    private static TaskManager tm;

    @BeforeEach
    public void setUp() {
        tm = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnListOfTasks(){
        Task task = new Task("задача типовая","описание");
        tm.addTask(task);
        int taskId = task.getId();
        tm.getTaskById(taskId);
        List<Task>list = tm.getHistory();
        assertEquals(1, list.size(),"Ожидался список из одной задачи");
    }

    @Test
    public void shoulldReturnEqualsFields(){
        Task task1 = new Task("задача типовая","описание", 1, Status.NEW);
        tm.addTask(task1);
        int taskId = task1.getId();
        Task taskFromTm= tm.getTaskById(taskId);
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
        Task task2 = new Task("Заголовок1", "Описание1",1,Status.IN_PROGRESS);
        tm.addTask(task1);
        int taskId1 = task1.getId();
        tm.addTask(task2);
        int taskId2 = task2.getId();
        assertFalse(taskId1 == taskId2,"Идентификаторы задач совпадают");
    }

}

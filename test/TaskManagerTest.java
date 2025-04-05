import enums.Status;
import exception.TaskOverlapEception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected EpicTask epic;
    protected SubTask subTask1;
    protected SubTask subTask2;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();

        task = new Task("Task title", "Task description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task);
        epic = new EpicTask("Epic title", "Epic description", Status.NEW);
        taskManager.addEpicTask(epic);
        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW,
                epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
    }

    @Test
    public void shouldAddAndGetTask() {
        //taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    public void shouldAddAndGetEpic() {
        // taskManager.addEpicTask(epic);
        EpicTask savedEpic = taskManager.getEpicTaskById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        List<EpicTask> epics = taskManager.getEpicsTasks();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    public void shouldAddAndGetSubTask() {
        SubTask savedSubTask = taskManager.getSubTaskById(subTask2.getId());
        assertNotNull(savedSubTask, "Подзадача не найдена");
        assertEquals(subTask2, savedSubTask, "Подзадачи не совпадают");

        List<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач");
        assertEquals(subTask1, subTasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    public void shouldUpdateTask() {
        Task updatedTask = new Task(task.getId(), "Обновление заголовка", "Обновление описания",
                Status.IN_PROGRESS, Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(3));
        taskManager.updateTask(updatedTask);

        assertEquals(updatedTask, taskManager.getTaskById(task.getId()), "Задача не обновлена");
    }

    @Test
    public void shouldUpdateEpic() {
        taskManager.addEpicTask(epic);
        EpicTask updatedEpic = new EpicTask(epic.getId(), "Updated title", "Updated description", Status.DONE);
        taskManager.updateEpicTask(updatedEpic);

        assertEquals(updatedEpic, taskManager.getEpicTaskById(epic.getId()), "Эпик не обновлен");
    }

    @Test
    public void shouldUpdateSubTask() {
        SubTask updatedSubTask = new SubTask(subTask1.getId(), "Updated title", "Updated description",
                Status.DONE, subTask1.getEpicId(),
                Duration.ofMinutes(20), LocalDateTime.now().plusHours(3));
        taskManager.updateSubTask(updatedSubTask);

        assertEquals(updatedSubTask, taskManager.getSubTaskById(subTask1.getId()), "Подзадача не обновлена");
    }

    @Test
    public void shouldRemoveTaskById() {
        int taskId = task.getId();
        taskManager.removeTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId), "Задача не удалена");
    }

    @Test
    public void shouldRemoveEpicById() {
        int epicId = epic.getId();
        taskManager.removeEpicTaskById(epicId);

        assertNull(taskManager.getEpicTaskById(epicId), "Эпик не удален");
        assertEquals(0, taskManager.getSubTasks().size(), "Подзадачи эпика не удалены");
    }

    @Test
    public void shouldRemoveSubTaskById() {
        int subTaskId = subTask1.getId();
        taskManager.removeSubTaskById(subTaskId);

        assertNull(taskManager.getSubTaskById(subTaskId), "Подзадача не удалена");
        assertEquals(1, taskManager.getEpicTaskById(epic.getId()).getSubTaskIds().size(),
                "Подзадача не удалена из эпика");
    }

    @Test
    public void shouldGetPrioritizedTasks() {

        List<Task> prioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());
        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач в списке приоритетов");
        assertEquals(subTask1, prioritizedTasks.get(0), "Неверный порядок задач");
        assertEquals(task, prioritizedTasks.get(1), "Неверный порядок задач");
        assertEquals(subTask2, prioritizedTasks.get(2), "Неверный порядок задач");
    }

    @Test
    public void shouldNotAllowTimeOverlap() {

        Task overlappingTask = new Task("Наезжаем на время", "Описание", Status.NEW,
                Duration.ofMinutes(120), task.getStartTime().plusMinutes(15));

        assertThrows(TaskOverlapEception.class, () -> taskManager.addTask(overlappingTask),
                "Должно быть исключение при пересечении времени");
    }

    // Тесты для статуса эпика
    @Test
    public void epicStatusShouldBeNewWhenAllSubtasksNew() {
        assertEquals(Status.NEW, taskManager.getEpicTaskById(epic.getId()).getStatus(),
                "Статус эпика должен быть NEW");
    }

    @Test
    public void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertEquals(Status.DONE, taskManager.getEpicTaskById(epic.getId()).getStatus(),
                "Статус эпика должен быть DONE");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksNewAndDone() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicTaskById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenAnySubtaskInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicTaskById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void shouldAddTaskToHistory() {
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task, history.get(0), "Задача в истории не совпадает");
    }

    @Test
    public void shouldNotDuplicateTaskInHistory() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История не должна содержать дубликатов");
    }

    @Test
    public void shouldRemoveTaskFromHistoryWhenDeleted() {
        taskManager.getTaskById(task.getId());
        taskManager.removeTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "История должна быть пустой после удаления задачи");
    }

}
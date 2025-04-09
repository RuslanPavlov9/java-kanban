import org.junit.jupiter.api.Test;
import tasks.EpicTask;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldCalculateEpicTimeFields() {
        EpicTask savedEpic = taskManager.getEpicTaskById(epic.getId());
        assertEquals(subTask1.getStartTime(), savedEpic.getStartTime(), "Неверное время начала эпика");
        assertEquals(subTask2.getEndTime(), savedEpic.getEndTime(), "Неверное время окончания эпика");
        assertEquals(Duration.between(subTask1.getStartTime(), subTask2.getEndTime()),
                savedEpic.getDuration(), "Неверная продолжительность эпика");
    }
}
import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private final String title = "Подзадача";
    private final String description = "Описание подзадачи.";
    private final int id = 1;
    private final Status status = Status.NEW;
    private final int epic_id = 2;
    private final Duration duration = Duration.ofHours(2);
    private final LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);

    private SubTask subTask;

    @BeforeEach
    void setUp() {
        subTask = new SubTask(id, title, description, status, epic_id, duration, startTime);
    }

    @Test
    void testGetEpicId() {
        assertEquals(epic_id, subTask.getEpicId(), "Epic ID согласно установленному значению.");
    }

    @Test
    void testConstructorWithTitleDescriptionAndEpicId() {
        SubTask subTask = new SubTask(title, description, epic_id);
        assertEquals(epic_id, subTask.getEpicId(), "Epic ID должен быть корректным.");
        assertNull(subTask.getDuration(), "Duration должен быть null при создании без параметров времени");
        assertNull(subTask.getStartTime(), "StartTime должен быть null при создании без параметров времени");
    }

    @Test
    void testConstructorWithTimeParameters() {
        SubTask subTaskWithTime = new SubTask(id, title, description, status, epic_id, duration, startTime);
        assertEquals(duration, subTaskWithTime.getDuration(), "Duration должен быть установлен");
        assertEquals(startTime, subTaskWithTime.getStartTime(), "StartTime должен быть установлен");
    }

    @Test
    void testToString() {
        String expectedString = "Подзадача{" +
                "Имя задачи='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id +
                ", epicId=" + epic_id +
                ", статус=" + status +
                ", продолжительность=" + duration +
                ", время начала=" + startTime +
                '}';
        assertEquals(expectedString, subTask.toString(), "toString должно вернуть корректное значение");
    }

    @Test
    void testToStringWithoutTime() {
        SubTask subTaskWithoutTime = new SubTask(title, description, id, status, epic_id);
        String expectedString = "Подзадача{" +
                "Имя задачи='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id +
                ", epicId=" + epic_id +
                ", статус=" + status +
                ", продолжительность=null" +
                ", время начала=null" +
                '}';
        assertEquals(expectedString, subTaskWithoutTime.toString(),
                "toString должно вернуть корректное значение без времени");
    }

    @Test
    void testGetEndTime() {
        LocalDateTime expectedEndTime = startTime.plus(duration);
        assertEquals(expectedEndTime, subTask.getEndTime(), "Время окончания должно корректно рассчитываться");
    }

    @Test
    void testGetEndTimeWhenNoStartTime() {
        SubTask taskWithoutTime = new SubTask(title, description, id, status, epic_id);
        assertNull(taskWithoutTime.getEndTime(), "При отсутствии времени начала должен возвращаться null");
    }
}
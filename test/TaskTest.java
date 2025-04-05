import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private final String title = "Заголовок задачи";
    private final String description = "Описание задачи";
    private final int id = 1;
    private final Status status = Status.IN_PROGRESS;
    private final Duration duration = Duration.ofHours(2);
    private final LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);

    private Task taskWithAllFields;
    private Task taskWithTitleAndDescription;
    private Task taskWithTimeFields;

    @BeforeEach
    void setUp() {
        taskWithAllFields = new Task(title, description, id, status);
        taskWithTitleAndDescription = new Task(title, description);
        taskWithTimeFields = new Task(id, title, description, status, duration, startTime);
    }

    @Test
    void testGetTitle() {
        assertEquals(title, taskWithAllFields.getTitle(), "Заголовок должен соответствовать установленному значению.");
        assertEquals(title, taskWithTitleAndDescription.getTitle(), "Заголовок должен соответствовать установленному значению.");
    }

    @Test
    void testGetId() {
        assertEquals(id, taskWithAllFields.getId(), "Идентификатор должен соответствовать установленному значению.");
        assertEquals(0, taskWithTitleAndDescription.getId(), "Если идентификатор не выставлялся, он должен быть по умолчанию (0).");
    }

    @Test
    void testGetDescription() {
        assertEquals(description, taskWithAllFields.getDescription(), "Описание должно соответствовать установленному значению.");
        assertEquals(description, taskWithTitleAndDescription.getDescription(), "Описание должно соответствовать установленному значению.");
    }

    @Test
    void testGetStatus() {
        assertEquals(status, taskWithAllFields.getStatus(), "Статус должен соответствовать установленному значению.");
        assertEquals(Status.NEW, taskWithTitleAndDescription.getStatus(), "Статус должен быть НОВЫЙ, если не указан.");
    }

    @Test
    void testGetDuration() {
        assertNull(taskWithAllFields.getDuration(), "Duration должен быть null, если не установлен");
        assertEquals(duration, taskWithTimeFields.getDuration(), "Duration должен соответствовать установленному значению");
    }

    @Test
    void testGetStartTime() {
        assertNull(taskWithAllFields.getStartTime(), "StartTime должен быть null, если не установлен");
        assertEquals(startTime, taskWithTimeFields.getStartTime(), "StartTime должен соответствовать установленному значению");
    }

    @Test
    void testGetEndTime() {
        assertNull(taskWithAllFields.getEndTime(), "EndTime должен быть null, если не установлены временные параметры");
        LocalDateTime expectedEndTime = startTime.plus(duration);
        assertEquals(expectedEndTime, taskWithTimeFields.getEndTime(), "EndTime должен корректно рассчитываться");
    }

    @Test
    void testSetTitle() {
        String newTitle = "Заголовок новый";
        taskWithAllFields.setTitle(newTitle);
        assertEquals(newTitle, taskWithAllFields.getTitle(), "Должно обновить заголовок");
    }

    @Test
    void testSetDescription() {
        String newDescription = "Новое описание";
        taskWithAllFields.setDescription(newDescription);
        assertEquals(newDescription, taskWithAllFields.getDescription(), "Ожидалось обновление описания.");
    }

    @Test
    void testSetStatus() {
        Status newStatus = Status.DONE;
        taskWithAllFields.setStatus(newStatus);
        assertEquals(newStatus, taskWithAllFields.getStatus(), "Ожидается обновление статуса");
    }

    @Test
    void testSetDuration() {
        Duration newDuration = Duration.ofHours(3);
        taskWithAllFields.setDuration(newDuration);
        assertEquals(newDuration, taskWithAllFields.getDuration(), "Ожидается обновление duration");
    }

    @Test
    void testSetStartTime() {
        LocalDateTime newStartTime = LocalDateTime.now();
        taskWithAllFields.setStartTime(newStartTime);
        assertEquals(newStartTime, taskWithAllFields.getStartTime(), "Ожидается обновление startTime");
    }

    @Test
    void testSetId() {
        int newId = 2;
        assertEquals(newId, taskWithAllFields.setId(newId), "Айди должен быть обновлен и возвращен корректным.");
        assertEquals(newId, taskWithAllFields.getId(), "Должен обновиться айди");
    }

    @Test
    void testToStringWithAllFields() {
        String expectedString = "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id +
                ", статус='" + status + '\'' +
                ", продолжительность=" + duration +
                ", время начала=" + startTime +
                '}';
        assertEquals(expectedString, taskWithTimeFields.toString(),
                "Порядок полей в toString должен соответствовать порядку в конструкторе");
    }

    @Test
    void testToStringWithoutTime() {
        String expectedString = "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id +
                ", статус='" + status + '\'' +
                ", продолжительность=null" +
                ", время начала=null" +
                '}';
        assertEquals(expectedString, taskWithAllFields.toString(),
                "Порядок полей должен сохраняться даже при null значениях");
    }

    @Test
    void testToStringWithMinimalFields() {
        String expectedString = "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=0" +
                ", статус='NEW'" +
                ", продолжительность=null" +
                ", время начала=null" +
                '}';
        assertEquals(expectedString, taskWithTitleAndDescription.toString(),
                "Порядок полей должен сохраняться для минимального конструктора");
    }

    @Test
    void testConstructorWithTimeParameters() {
        Task task = new Task(id, title, description, status, duration, startTime);
        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(status, task.getStatus());
        assertEquals(duration, task.getDuration());
        assertEquals(startTime, task.getStartTime());
    }

}
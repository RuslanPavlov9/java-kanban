import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private final String title = "Заголовок задачи";
    private final String description = "Описание задачи";
    private final int id = 1;
    private final Status status = Status.IN_PROGRESS;

    private Task taskWithAllFields;
    private Task taskWithTitleAndDescription;

    @BeforeEach
    void setUp() {
        taskWithAllFields = new Task(title, description, id, status);
        taskWithTitleAndDescription = new Task(title, description);
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
                '}';
        assertEquals(expectedString, taskWithAllFields.toString(), "toString должен возвращать корректные сообщения");
    }

    @Test
    void testToStringWithoutIdAndStatus() {
        String expectedString = "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id=0" +
                ", статус='NEW'" +
                '}';
        assertEquals(expectedString, taskWithTitleAndDescription.toString(), "toString должен возвращать корректные сообщения без айди и статуса.");
    }

    @Test
    void testEquals() {
        Task task1 = new Task(title, description, id, status);
        Task task2 = new Task(title, description, id, status);

        assertTrue(task1.equals(task2), "Задачи с одинаковыми полями должны быть равнозначными.");

        Task task3 = new Task("Новый заголовок", description, id, status);
        assertFalse(task1.equals(task3), "Задачи с разными названиями не должны быть одинаковыми.");

        Task task4 = new Task(title, "Новое описание", id, status);
        assertFalse(task1.equals(task4), "Tasks with different descriptions should not be equal.");

        Task task5 = new Task(title, description, 2, status);
        assertFalse(task1.equals(task5), "Задачи с разными идентификаторами не должны быть равными.");

        Task task6 = new Task(title, description, id, Status.DONE);
        assertFalse(task1.equals(task6), "Задачи с разным статусом не должны быть равными.");
    }

    @Test
    void testHashCode() {
        Task task1 = new Task(title, description, id, status);
        Task task2 = new Task(title, description, id, status);
        assertEquals(task1.hashCode(), task2.hashCode(), "Хеш-коды для одинаковых задач должны быть одинаковыми.");

        Task task3 = new Task("Новый заголовок", description, id, status);
        assertNotEquals(task1.hashCode(), task3.hashCode(), "Хэш-коды для задач с разными названиями должны быть разными..");

        Task task4 = new Task(title, "Новое описание", id, status);
        assertNotEquals(task1.hashCode(), task4.hashCode(), "Хеш-коды для задач с разным описанием должны быть разными.");

        Task task5 = new Task(title, description, 2, status);
        assertNotEquals(task1.hashCode(), task5.hashCode(), "Хэш-коды для задач с разными идентификаторами должны быть разными..");

        Task task6 = new Task(title, description, id, Status.DONE);
        assertNotEquals(task1.hashCode(), task6.hashCode(), "Хэш-коды для задач с разными статусами должны быть разными.");
    }
}
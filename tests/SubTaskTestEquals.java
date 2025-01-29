import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTestEquals {

    private final String title = "Заголовок подзадачи";
    private final String description = "Описание подзадачи.";
    private final int id1 = 1;
    private final int id2 = 2;
    private final Status status = Status.NEW;
    private final int epic_id = 10;

    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void setUp() {
        subTask1 = new SubTask(title, description, id1, status, epic_id);
        subTask2 = new SubTask("Задача 2", "Описание задачи 2", id1, Status.IN_PROGRESS, epic_id + 1);
        subTask3 = new SubTask(title, description, id2, status, epic_id);
    }

    @Test
    void testEqualsWithSameId() {
            assertFalse(subTask1.equals(subTask2), "Подзадачи с одинаковым идентификатором должны быть различны.");
    }

    @Test
    void testNotEqualsWithDifferentIds() {
        assertFalse(subTask1.equals(subTask3), "Подзадачи с разными идентификаторами не должны быть равными.");
    }

    @Test
    void testHashCodeWithDifferentIds() {
        assertNotEquals(subTask1.hashCode(), subTask3.hashCode(), "Хэш-коды для подзадач с разными идентификаторами должны быть разными.");
    }

    @Test
    void testGetEpicId() {
        assertEquals(epic_id, subTask1.getEpicId(), "Epic ID должен соответствовать установленному значению.");
    }
}
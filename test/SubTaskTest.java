
import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private final String title = "Подзадача";
    private final String description = "Описание подзадачи.";
    private final int id = 1;
    private final Status status = Status.NEW;
    private final int epic_id = 2;

    private SubTask subTask;

    @BeforeEach
    void setUp() {
        subTask = new SubTask(title, description, id, status, epic_id);
    }

    @Test
    void testGetEpicId() {
        assertEquals(epic_id, subTask.getEpicId(), "Epic ID согласно установленному значению.");
    }

    @Test
    void testConstructorWithTitleDescriptionAndEpicId() {
        SubTask subTask = new SubTask(title, description, epic_id);
        assertEquals(epic_id, subTask.getEpicId(), "Epic ID должен быть корректным.");
    }

    @Test
    void testToString() {
        String expectedString = "Подзадача{Имя задачи='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", epicId=" + epic_id +
                ", status=" + status +
                '}';
        assertEquals(expectedString, subTask.toString(), "toString должно вернуть корректное значение");
    }


}
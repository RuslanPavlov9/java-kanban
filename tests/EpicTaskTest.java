import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    private EpicTask epicTask;
    private final String name = "Эпик-задача";
    private final String description = "Описание эпика";
    private final int id = 1;
    private final Status status = Status.NEW;

    @BeforeEach
    void setUp() {
        epicTask = new EpicTask(name, description, id, status);
    }

    @Test
    void testGetSubTaskIdsInitiallyEmpty() {
        assertTrue(epicTask.getSubTaskIds().isEmpty(), "После запуска идентификаторы подзадач должны быть пустыми.");
    }

    @Test
    void testSetSubTaskIds() {
        List<Integer> subTaskIds = List.of(2, 3, 4);
        epicTask.setSubTaskIds(subTaskIds);
        assertEquals(subTaskIds, epicTask.getSubTaskIds(), "Идентификаторы подзадач должны соответствовать установленному значению.");
    }

    @Test
    void testAddSubTaskId() {
        epicTask.addSubTaskId(2);
        assertEquals(List.of(2), epicTask.getSubTaskIds(), "Идентификатор только один");

        epicTask.addSubTaskId(3);
        assertEquals(List.of(2, 3), epicTask.getSubTaskIds(), "После добавления еще одной подзадачи должно стать два идентификатора подзадач.");
    }

    @Test
    void testClearSubTask() {
        epicTask.addSubTaskId(2);
        epicTask.addSubTaskId(3);
        epicTask.clearSubTask();
        assertTrue(epicTask.getSubTaskIds().isEmpty(), "Ожидается что не останется ни одного сабтаск айди");
    }

    @Test
    void testToStringWithoutSubTasks() {
        String expectedString = "Эпик{название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + "}";
        assertEquals(expectedString, epicTask.toString(), "toString должен возвращать корректную строку. без подзадач.");
    }

    @Test
    void testToStringWithSubTasks() {
        List<Integer> subTaskIds = List.of(2, 3);
        epicTask.setSubTaskIds(subTaskIds);

        String expectedString = "Эпик{название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", id подзадачи=" + subTaskIds + "}";
        assertEquals(expectedString, epicTask.toString(), "toString должен возвращать корректную строку. с подзадачами.");
    }
}
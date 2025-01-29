import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTestEquals {

    private final String title = "Эпик 1";
    private final String description = "Описание эпика 1";
    private final int id1 = 1;
    private final int id2 = 2;
    private final Status STATUS = Status.NEW; // Предположим, что у вас есть такой статус

    private EpicTask epicTask1;
    private EpicTask epicTask2;
    private EpicTask epicTask3;

    @BeforeEach
    void setUp() {
        epicTask1 = new EpicTask(title, description, id1, STATUS);
        epicTask2 = new EpicTask("Эпик 2", "Описание Эпика 2", id1, Status.IN_PROGRESS); // Same ID as epicTask1
        epicTask3 = new EpicTask(title, description, id2, STATUS); // Different ID
    }

    @Test
    void testEqualsWithSameId() {
        assertFalse(epicTask1.equals(epicTask2), "Эпики с одинаковыми айди различны");
    }

    @Test
    void testNotEqualsWithDifferentIds() {
        assertFalse(epicTask1.equals(epicTask3), "Эпики с разными айди не равны.");
    }

    @Test
    void testHashCodeWithDifferentIds() {
        assertNotEquals(epicTask1.hashCode(), epicTask3.hashCode(), "Хэш коды эпиков с разными айди - различны.");
    }

}
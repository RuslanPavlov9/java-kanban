import org.junit.jupiter.api.Test;
import taskmanagers.InMemoryHistoryManager;
import taskmanagers.InMemoryTaskManager;
import taskmanagers.Managers;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getInMemoryHistoryManager());
    }
}
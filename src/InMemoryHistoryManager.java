import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasksHistory = new ArrayList<Task>();
    private static final int HISTORY_DEPTH = 10;

    @Override
    public void add(Task task) {
    if (tasksHistory.size() == HISTORY_DEPTH) {
        tasksHistory.removeFirst();
    }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

}

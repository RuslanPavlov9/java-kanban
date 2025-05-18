package taskmanagers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpicTask(EpicTask epicTask);

    void updateSubTask(SubTask subTask);

    Task getTaskById(int id);

    EpicTask getEpicTaskById(int epicId);

    SubTask getSubTaskById(int subId);

    ArrayList<Task> getTasks();

    ArrayList<EpicTask> getEpicsTasks();

    ArrayList<SubTask> getSubTasks();

    List<SubTask> getSubtasksByEpic(int epicId);

    void removeTaskById(int id);

    void removeAllTasks();

    void deleteEpics();

    void removeEpicTaskById(int epicId);

    void removeSubTaskById(int subTaskId);

    void deleteSubtasksSetEpicStatus();

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

}

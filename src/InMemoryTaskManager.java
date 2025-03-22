import enums.Status;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final InMemoryHistoryManager historyManager = Managers.getInMemoryHistoryManager();
    private int nextId = 1;

    @Override
    public void addTask(Task task) {
        task.setId(getNextId());
        int taskId = task.getId();
        tasks.put(taskId, task);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        epicTask.setId(getNextId());
        int epicTaskId = epicTask.getId();
        epicTasks.put(epicTaskId, epicTask);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        EpicTask epicTask = epicTasks.get(subTask.getEpicId());
        subTask.setId(getNextId());
        int subTaskId = subTask.getId();
        epicTask.addSubTaskId(subTaskId);
        subTasks.put(subTaskId, subTask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        checkEpicStatus(epicTask.getId());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTasks.get(subTask.getId()).getEpicId();
        checkEpicStatus(epicId);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int subId) {
        SubTask subTask = subTasks.get(subId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getEpicsTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubtasksByEpic(EpicTask epicTask) {
        List<Integer> subtaskIds = epicTask.getSubTaskIds();
        List<SubTask> subTasksByEpic = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            subTasksByEpic.add(subTasks.get(subtaskId));
        }
        return subTasksByEpic;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void removeEpicTaskById(int epicId) {
        List<Integer> subtaskIds = epicTasks.get(epicId).getSubTaskIds();
        for (Integer subtaskId : subtaskIds) {
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epicTasks.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void removeSubTaskById(int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();
        List<Integer> subtaskIds = epicTasks.get(epicId).getSubTaskIds();
        subtaskIds.remove((Integer) subTaskId);
        subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);
        checkEpicStatus(epicId);
    }

    @Override
    public void deleteSubtasksSetEpicStatus() {
        subTasks.clear();
        for (EpicTask epic : epicTasks.values()) {
            epic.clearSubTask();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void checkEpicStatus(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        List<Integer> subtaskIds = epicTask.getSubTaskIds();

        long countNew = subtaskIds.stream()
                .filter(subtaskId -> subTasks.get(subtaskId).getStatus() == Status.NEW)
                .count();
        long countDone = subtaskIds.stream()
                .filter(subtaskId -> subTasks.get(subtaskId).getStatus() == Status.DONE)
                .count();

        if (subtaskIds.isEmpty() || countNew == subtaskIds.size()) {
            epicTask.setStatus(Status.NEW);
        } else if (countDone == subtaskIds.size()) {
            epicTask.setStatus(Status.DONE);
        } else {
            epicTask.setStatus(Status.IN_PROGRESS);
        }
    }

    private int getNextId() {
        return nextId++;
    }

}



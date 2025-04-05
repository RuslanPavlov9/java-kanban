import enums.Status;
import exception.TaskOverlapEception;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    public final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final InMemoryHistoryManager historyManager = Managers.getInMemoryHistoryManager();
    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    protected int nextId = 1;

    @Override
    public void addTask(Task task) {
        if (!isTimeOverlap(task)) {
            checkAndSetId(task);
            int taskId = task.getId();
            tasks.put(taskId, task);
            addToPriority(task);
        } else throw new TaskOverlapEception("Обновлённая задача пересекается по времени с другой задачей!");
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        checkAndSetId(epicTask);
        int epicTaskId = epicTask.getId();
        epicTasks.put(epicTaskId, epicTask);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (!isTimeOverlap(subTask)) {
            checkAndSetId(subTask);
            EpicTask epicTask = epicTasks.get(subTask.getEpicId());
            int subTaskId = subTask.getId();
            epicTask.addSubTaskId(subTaskId);
            subTasks.put(subTaskId, subTask);
            checkEpicStatus(subTask.getEpicId());
            updateEpicTimeFields(epicTask.getId());
            addToPriority(subTask);
        } else throw new TaskOverlapEception("Обновлённая задача пересекается по времени с другой задачей!");
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        removeFromPriority(oldTask);

        if (!isTimeOverlap(task)) {
            tasks.put(task.getId(), task);
            addToPriority(task);
        } else {
            addToPriority(oldTask);
            throw new TaskOverlapEception("Обновлённая задача пересекается по времени с другой задачей!");
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        checkEpicStatus(epicTask.getId());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Task oldTask = subTasks.get(subTask.getId());
        removeFromPriority(oldTask);

        if (!isTimeOverlap(subTask)) {
            int subTaskId = subTask.getId();
            subTasks.put(subTaskId, subTask);
            int epicId = subTasks.get(subTask.getId()).getEpicId();
            updateEpicTimeFields(epicId);
            checkEpicStatus(subTask.getEpicId());
            addToPriority(subTask);
        } else {
            addToPriority(oldTask);
            throw new TaskOverlapEception("Обновлённая задача пересекается по времени с другой задачей!");
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask != null) historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int subId) {
        SubTask subTask = subTasks.get(subId);
        if (subTask != null) historyManager.add(subTask);
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
        return epicTask.getSubTaskIds().stream()
                .map(subTaskId -> subTasks.get(subTaskId))
                .toList();
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким ID нет");
        } else {
            tasks.remove(id);
            historyManager.remove(id);
            removeFromPriority(getTaskById(id));
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteEpics() {
        epicTasks.clear();
        subTasks.clear();
        prioritizedTasks.clear();
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
        updateEpicTimeFields(epicId);
        checkEpicStatus(epicId);
        removeFromPriority(getSubTaskById(subTaskId));
    }

    @Override
    public void deleteSubtasksSetEpicStatus() {
        subTasks.clear();
        epicTasks.values().forEach(epic -> {
            epic.clearSubTask();
            epic.setStatus(Status.NEW);
        });
    }

    public TreeSet<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
        return prioritizedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    void checkEpicStatus(int epicId) {
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

    @Override
    public boolean isTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(existingTask ->
                        newTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                                newTask.getEndTime().isAfter(existingTask.getStartTime())
                );
    }

    void updateEpicTimeFields(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        List<SubTask> subTasksList = getSubtasksByEpic(epic);

        if (subTasksList.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }

        LocalDateTime earliestStart = subTasksList.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subTasksList.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = null;
        if (earliestStart != null && latestEnd != null) {
            totalDuration = Duration.between(earliestStart, latestEnd);
        }

        epic.setStartTime(earliestStart);
        epic.setDuration(totalDuration);
    }

    private <T extends Task> void checkAndSetId(T task) {
        if (task.getId() < getCurrentId()) {
            task.setId(getNextId());
        } else {
            System.out.println("ID уже задан: " + task.getId());
        }
    }

    private int getNextId() {
        return nextId++;
    }

    private int getCurrentId() {
        return nextId;
    }

    private void removeFromPriority(Task task) {
        try {
            prioritizedTasks.remove(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToPriority(Task task) {
        prioritizedTasks.add(task);
    }

}



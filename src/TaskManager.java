import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager{
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int nextId = 1;


    public int getNextId() {
        return nextId++;
    }

    public void addTask(Task task) {
        int id = task.setId(getNextId());
        tasks.put(id, task);
    }

    public void addEpicTask(EpicTask epicTask) {
        int id = epicTask.setId(getNextId());
        epicTasks.put(id, epicTask);
    }

    public void addSubTask (SubTask subTask) {
        EpicTask epicTask = epicTasks.get(subTask.getEpicId());
        int subTaskId = subTask.setId(getNextId());
        epicTask.addSubTaskId(subTaskId);
        subTasks.put(subTaskId, subTask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        checkEpicStatus(epicTask.getId());
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTasks.get(subTask.getId()).getEpicId();
        checkEpicStatus(epicId);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTaskById(int epicId) {
        return epicTasks.get(epicId);
    }

    public SubTask getSubTaskById(int subId) {
        return subTasks.get(subId);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getEpicsTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getSubTasks() {return new ArrayList<>(subTasks.values());}

    public List<Integer> getEpicSubtasksIds(EpicTask epicTask) {
        return epicTask.getSubTaskIds();
    }

    public List<SubTask> getSubtasksByEpic(EpicTask epicTask) {
        List <Integer> subtaskIds = epicTask.getSubTaskIds();
        List <SubTask> subTasksByEpic = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            subTasksByEpic.add(subTasks.get(subtaskId));
        }
        return subTasksByEpic;
    }
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeAllTasks(){
        tasks.clear();
    }

    public void deleteEpics() {
        epicTasks.clear();
        subTasks.clear();
    }

    public void removeEpicTaskById(int epicId) {
        List <Integer> subtaskIds = epicTasks.get(epicId).getSubTaskIds();
        for (Integer subtaskId : subtaskIds) {
            subTasks.remove(subtaskId);
        }
        epicTasks.remove(epicId);
    }

    public void removeSubTaskById(int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();
        List<Integer> subtaskIds = epicTasks.get(epicId).getSubTaskIds();
        subtaskIds.remove((Integer) subTaskId);
        subTasks.remove(subTaskId);
        checkEpicStatus(epicId);
    }

    public void deleteSubtasksSetEpicStatus() {
        subTasks.clear();
        for (EpicTask epic : epicTasks.values()) {
            epic.clearSubTask();
            epic.setStatus(Status.NEW);
        }
    }

    public void checkEpicStatus(int epicId) {
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

}



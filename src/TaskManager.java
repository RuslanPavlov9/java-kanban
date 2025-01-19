import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager{
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int nextId = 1;


    //Создание. Сам объект должен передаваться в качестве параметра.
    public void addTask(Task task) {
        tasks.put(task.setId(nextId++), task);
    }

    public void addEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.setId(nextId++), epicTask);
    }

    public void addSubTask (SubTask subTask) {
        EpicTask epicTask = epicTasks.get(subTask.getEpicId());
        int subTaskId = subTask.setId(nextId++);
        epicTask.addSubTaskId(subTaskId);
        subTasks.put(subTaskId, subTask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
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

    public void removeAllTasks(){
            tasks.clear();
            subTasks.clear();
            epicTasks.clear();
            System.out.println("Все задачи удалены.");
    }

    public void printTasks() {
        tasks.forEach((key, value) -> {System.out.println(key + ": " + value);});
    }
    public void printEpicTasks() {
        epicTasks.forEach((key, value) -> {System.out.println(key + ": " + value);});
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



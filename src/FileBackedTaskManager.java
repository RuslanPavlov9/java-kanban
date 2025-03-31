import enums.Status;
import enums.TaskType;
import exception.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        File file = new File(".idea/resources/tasks.csv");
        FileBackedTaskManager fileManager = loadFromFile(file);

        System.out.println("getTasks,getEpicsTasks,getSubTasks");
        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpicsTasks());
        System.out.println(fileManager.getSubTasks());

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Пропускаем заголовок
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof EpicTask) {
                    fileManager.addEpicTask((EpicTask) task);
                } else if (task instanceof SubTask) {
                    fileManager.addSubTask((SubTask) task);
                } else {
                    fileManager.addTask(task);
                }
            }
            fileManager.nextId = maxId + 1;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных из файла", e);
        }
        return fileManager;
    }

    @Override
    public void addTask(Task task) {
        int taskId = task.getId();
        tasks.put(taskId, task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        int epicTaskId = epicTask.getId();
        epicTasks.put(epicTaskId, epicTask);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        tasks.put(taskId, task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        int epicTaskId = epicTask.getId();
        epicTasks.put(epicTaskId, epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void removeEpicTaskById(int id) {
        super.removeEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubtasksSetEpicStatus() {
        super.deleteSubtasksSetEpicStatus();
        save();
    }

    @Override
    public void removeSubTaskById(int subTaskId) {
        super.removeSubTaskById(subTaskId);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epicId\n");
            for (Task task : super.getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (EpicTask epic : super.getEpicsTasks()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    private String toString(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            return task.getId() + ",SUBTASK," + task.getTitle() + "," + task.getStatus() + "," +
                    task.getDescription() + "," + subTask.getEpicId();
        } else if (task instanceof EpicTask) {
            return task.getId() + ",EPIC," + task.getTitle() + "," + task.getStatus() + "," +
                    task.getDescription();
        } else {
            return task.getId() + ",TASK," + task.getTitle() + "," + task.getStatus() + "," +
                    task.getDescription();
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new EpicTask(id, name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new SubTask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

}

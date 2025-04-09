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
import java.time.Duration;
import java.time.LocalDateTime;


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
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                maxId = Math.max(maxId, task.getId());
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
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
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

    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,duration,startTime,epicId\n");
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
        String[] fields = new String[]{
                String.valueOf(task.getId()),
                task instanceof EpicTask ? "EPIC" : (task instanceof SubTask ? "SUBTASK" : "TASK"),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "\"\"",
                task.getStartTime() != null ? task.getStartTime().toString() : "\"\"",
                task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = parts[5].equals("\"\"") ? null :
                Duration.ofMinutes(Long.parseLong(parts[5]));

        LocalDateTime startTime = parts[6].equals("\"\"") ? null :
                LocalDateTime.parse(parts[6]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                return new EpicTask(id, name, description, status, duration, startTime);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7]);
                return new SubTask(id, name, description, status, epicId, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

}
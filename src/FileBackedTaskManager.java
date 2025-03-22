import enums.Status;
import enums.TaskType;
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
    private boolean isLoading = false; // Флаг для отслеживания загрузки данных

    public FileBackedTaskManager(File file) {
        this.file = file;
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

    public void save() {
        if (isLoading) {
            return; // Пропускаем сохранение, если идет загрузка данных
        }
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

    public void loadFromFile() {
        isLoading = true; // Устанавливаем флаг загрузки
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Пропускаем заголовок
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println(counter++);
                Task task = fromString(line);
                if (task instanceof EpicTask) {
                    addEpicTask((EpicTask) task);
                } else if (task instanceof SubTask) {
                    addSubTask((SubTask) task);
                } else {
                    addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных из файла", e);
        } finally {
            isLoading = false; // Сбрасываем флаг загрузки
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

    // Метод для создания задачи из строки
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

    // Кастомное исключение
    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

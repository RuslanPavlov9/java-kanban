import enums.Status;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    static File file = new File(".idea/resources/tasks.csv");
    static final FileBackedTaskManager taskManager = Managers.getDefaultFileBackedTM(file);

    public static void main(String[] args) {

        Task task1 = new Task("Заголовок1", "Описание задачи 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        Task task2 = new Task("Заголовок2", "Описание задачи 1", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now().plusHours(2));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1", Status.NEW);
        taskManager.addEpicTask(epicTask1);
        SubTask subtask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epicTask1.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(0));
        SubTask subtask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epicTask1.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        SubTask subtask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epicTask1.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(5));

        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);
        task1.setDuration(Duration.ofMinutes(31));
        taskManager.updateTask(task1);

        System.out.println("Приоритетные задачи:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

    }

}


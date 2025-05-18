package server;

import com.sun.net.httpserver.HttpServer;
import enums.Status;
import server.handlers.EpicHandler;
import server.handlers.HistoryHandler;
import server.handlers.PrioritizedHandler;
import server.handlers.SubtaskHandler;
import server.handlers.TaskHandler;
import taskmanagers.Managers;
import taskmanagers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
    }


    public static void main(String[] args) throws IOException {
        TaskManager taskManager = setUpTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    private static TaskManager setUpTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        EpicTask epic = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epic);
        SubTask subtask1 = new SubTask("Позадача 1", "Описание подзадачи 1", Status.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 5, 2, 14, 20));
        SubTask subtask2 = new SubTask("Позадача 2", "Описание подзадачи 2", Status.DONE, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 18, 40));
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 5, 5, 23, 45));
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 5, 15, 45));
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 5, 13, 20));
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        return taskManager;
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

}

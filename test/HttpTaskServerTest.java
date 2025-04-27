import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import taskmanagers.InMemoryTaskManager;
import tasks.Task;
import tasks.SubTask;
import tasks.EpicTask;
import enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private InMemoryTaskManager manager;
    private HttpClient client;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    // --- Тесты для /tasks ---

    @Test
    void getTasksSuccess() throws Exception {
        Task task = new Task("Test Task", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test Task"));
    }

    @Test
    void getTaskByIdSuccess() throws Exception {
        Task task = new Task("TaskById", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        manager.addTask(task);
        int id = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("TaskById"));
    }

    @Test
    void getTaskByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/999999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void postTaskBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void deleteTaskByIdSuccess() throws Exception {
        Task task = new Task("ToDelete", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        manager.addTask(task);
        int id = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно удалена"));
    }

    @Test
    void deleteAllTasksSuccess() throws Exception {
        Task task = new Task("ToDeleteAll", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Все задачи очищены"));
    }

    // --- Аналогично тесты для /subtasks ---

    @Test
    void getSubtasksSuccess() throws Exception {
        EpicTask epic = new EpicTask("Epic", "Desc");
        manager.addEpicTask(epic);
        SubTask subtask = new SubTask("Subtask", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(1));
        manager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    void getSubtaskByIdSuccess() throws Exception {
        EpicTask epic = new EpicTask("Epic", "Desc");
        manager.addEpicTask(epic);
        SubTask subtask = new SubTask("SubtaskById", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(1));
        manager.addSubTask(subtask);
        int id = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("SubtaskById"));
    }

    @Test
    void getSubtaskByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/999999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void postSubtaskBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void deleteSubtaskByIdSuccess() throws Exception {
        EpicTask epic = new EpicTask("Epic", "Desc");
        manager.addEpicTask(epic);
        SubTask subtask = new SubTask("ToDelete", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(1));
        manager.addSubTask(subtask);
        int id = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно удалена"));
    }

    @Test
    void deleteAllSubtasksSuccess() throws Exception {
        EpicTask epic = new EpicTask("Epic", "Desc");
        manager.addEpicTask(epic);
        SubTask subtask = new SubTask("ToDeleteAll", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(1));
        manager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно очищен"));
    }

    // --- Тесты для /epics ---

    @Test
    void getEpicsSuccess() throws Exception {
        EpicTask epic = new EpicTask("Epic1", "Desc");
        manager.addEpicTask(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic1"));
    }

    @Test
    void getEpicByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/999999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void getSubtasksByEpicIdSuccess() throws Exception {
        EpicTask epic = new EpicTask("EpicWithSubs", "Desc");
        manager.addEpicTask(epic);
        SubTask subtask = new SubTask("SubtaskInEpic", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(1));
        manager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/" + epic.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("SubtaskInEpic"));
    }

    @Test
    void postEpicAddSuccess() throws Exception {
        String json = """
                {
                  "id":0,
                  "title":"New Epic",
                  "description":"Desc"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("успешно добавлен"));
    }

    @Test
    void postEpicBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void deleteEpicByIdSuccess() throws Exception {
        EpicTask epic = new EpicTask("ToDeleteEpic", "Desc");
        manager.addEpicTask(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно удален"));
    }

    @Test
    void deleteAllEpicsSuccess() throws Exception {
        EpicTask epic = new EpicTask("ToDeleteAllEpics", "Desc");
        manager.addEpicTask(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно очищен"));
    }

    // --- Тесты для /history ---

    @Test
    void getHistorySuccess() throws Exception {
        Task task = new Task("HistoryTask", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        manager.addTask(task);
        manager.getTaskById(task.getId()); // чтобы добавить в историю

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("HistoryTask"));
    }

    // --- Тесты для /prioritized ---

    @Test
    void getPrioritizedSuccess() throws Exception {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now().plusDays(2));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task1"));
        assertTrue(response.body().contains("Task2"));
    }

}

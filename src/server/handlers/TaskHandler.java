package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import enums.Endpoint;
import exception.TaskOverlapEception;
import taskmanagers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getTasks(exchange);
            case GET_BY_ID -> getTask(exchange, getId(path));
            case POST -> postTask(exchange);
            case DELETE_BY_ID -> deleteTask(exchange, getId(path));
            case DELETE -> deleteTasks(exchange);
            case UNKNOWN -> sendBadRequest(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (path[1].equals("tasks") && path.length <= 3) {
            if (path.length == 3) {
                try {
                    Integer.parseInt(path[2]);
                } catch (NumberFormatException e) {
                    return Endpoint.UNKNOWN;
                }
            }
            switch (requestMethod) {
                case "GET":
                    if (path.length == 2) {
                        return Endpoint.GET;
                    } else {
                        return Endpoint.GET_BY_ID;
                    }
                case "POST":
                    return Endpoint.POST;
                case "DELETE":
                    if (path.length == 2) {
                        return Endpoint.DELETE;
                    } else {
                        return Endpoint.DELETE_BY_ID;
                    }
                default:
                    return Endpoint.UNKNOWN;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getTasks());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendTextResponse(exchange, jsonString, 200);
    }

    private void getTask(HttpExchange exchange, int id) throws IOException {
        Optional<Task> maybeTask = Optional.ofNullable(taskManager.getTaskById(id));
        if (maybeTask.isPresent()) {
            String jsonString = gson.toJson(maybeTask.get());
            sendTextResponse(exchange, jsonString, 200);
            return;
        }
        sendNotFound(exchange);
    }

    private void postTask(HttpExchange exchange) throws IOException {

        String stringTask = parseRequestBody(exchange);

        if (stringTask.isEmpty() || stringTask.isBlank()) {
            sendNotFound(exchange);
        }
        try {
            Task task = gson.fromJson(stringTask, Task.class);
            try {
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    sendTextResponse(exchange, "Задача успешно добавлена", 201);
                } else {
                    taskManager.updateTask(task);
                    sendTextResponse(exchange, "Задача успешно обновлена", 201);
                }
            } catch (TaskOverlapEception e) {
                sendHasInteractions(exchange);
            } catch (IllegalArgumentException e) {
                sendBadRequest(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.removeTaskById(id);
            sendTextResponse(exchange, "Задача успешно удалена", 200);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }

    private void deleteTasks(HttpExchange exchange) throws IOException {
        taskManager.removeAllTasks();
        sendTextResponse(exchange, "Все задачи очищены", 200);
    }

}

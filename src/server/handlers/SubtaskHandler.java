package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exception.TaskOverlapEception;
import taskmanagers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getSubtasks(exchange);
            case GET_BY_ID -> getSubtask(exchange, getId(path));
            case POST -> postSubtask(exchange);
            case DELETE_BY_ID -> deleteSubtask(exchange, getId(path));
            case DELETE -> deleteSubtasks(exchange);
            case UNKNOWN -> sendBadRequest(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (path[1].equals("subtasks") && path.length <= 3) {
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

    private void getSubtasks(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getSubTasks());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendResponse(exchange, jsonString, 200);
    }

    private void getSubtask(HttpExchange exchange, int id) throws IOException {
        Optional<SubTask> subtask = Optional.ofNullable(taskManager.getSubTaskById(id));
        if (subtask.isPresent()) {
            String jsonString = gson.toJson(subtask.get());
            sendResponse(exchange, jsonString, 200);
            return;
        }
        sendNotFound(exchange);
    }

    private void postSubtask(HttpExchange exchange) throws IOException {

        String stringSubtask = parseRequestBody(exchange);

        if (stringSubtask.isEmpty() || stringSubtask.isBlank()) {
            sendNotFound(exchange);
        }
        try {
            SubTask task = gson.fromJson(stringSubtask, SubTask.class);
            try {
                if (task.getId() == 0) {
                    taskManager.addSubTask(task);
                    sendTextResponse(exchange, "Подзадача успешно добавлена", 201);
                } else {
                    taskManager.updateSubTask(task);
                    sendTextResponse(exchange, "Подзадача успешно обновлена", 201);
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

    private void deleteSubtask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.removeSubTaskById(id);
            sendTextResponse(exchange, "Подзадача успешно удалена", 200);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }

    private void deleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasksSetEpicStatus();
        sendTextResponse(exchange, "Список подзадач успешно очищен", 200);
    }
}
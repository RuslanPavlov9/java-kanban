package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exception.TaskOverlapEception;
import taskmanagers.TaskManager;
import tasks.EpicTask;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getEpics(exchange);
            case GET_BY_ID -> getEpic(exchange, getId(path));
            case GET_EPICS_SUBTASKS_BY_ID -> getSubtasks(exchange, getId(path));
            case POST -> postEpic(exchange);
            case DELETE_BY_ID -> deleteEpic(exchange, getId(path));
            case DELETE -> deleteEpics(exchange);
            case UNKNOWN -> sendBadRequest(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (path[1].equals("epics") && path.length <= 4) {
            if (path.length >= 3) {
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
                    } else if (path.length == 4 && path[3].equals("subtasks")) {
                        return Endpoint.GET_EPICS_SUBTASKS_BY_ID;
                    } else if (path.length == 3) {
                        return Endpoint.GET_BY_ID;
                    } else {
                        return Endpoint.UNKNOWN;
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

    private void getSubtasks(HttpExchange exchange, int id) throws IOException {
        try {
            String jsonString = gson.toJson(taskManager.getSubtasksByEpic(id));
            if (jsonString.isEmpty()) {
                sendNotFound(exchange);
            }
            sendTextResponse(exchange, jsonString, 200);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        sendResponse(exchange, taskManager.getEpicsTasks(), 200);
    }

    private void getEpic(HttpExchange exchange, int id) throws IOException {
        Optional<EpicTask> epic = Optional.ofNullable(taskManager.getEpicTaskById(id));
        if (epic.isPresent()) {
            sendResponse(exchange, epic, 200);
        }
        sendNotFound(exchange);
    }

    private void postEpic(HttpExchange exchange) throws IOException {

        String stringEpic = parseRequestBody(exchange);

        if (stringEpic.isEmpty() || stringEpic.isBlank()) {
            sendNotFound(exchange);
        }
        try {
            EpicTask task = gson.fromJson(stringEpic, EpicTask.class);
            try {
                if (task.getId() == 0) {
                    taskManager.addEpicTask(task);
                    sendTextResponse(exchange, "Эпик успешно добавлен.", 201);
                } else {
                    taskManager.updateEpicTask(task);
                    sendTextResponse(exchange, "Эпик успешно обновлен.", 201);
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

    private void deleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.removeEpicTaskById(id);
            sendTextResponse(exchange, "Эпик успешно удален.", 200);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }

    private void deleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        sendTextResponse(exchange, "Список Эпиков успешно очищен.", 200);
    }

}

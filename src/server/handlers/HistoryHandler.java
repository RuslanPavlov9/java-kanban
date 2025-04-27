package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import taskmanagers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = getGson();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_HISTORY -> sendHistory(exchange);
            default -> sendBadRequest(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (path.length == 2 && path[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    private void sendHistory(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getHistory());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendResponse(exchange, jsonString, 200);
    }
}
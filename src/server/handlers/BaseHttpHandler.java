package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import serializers.DurationAdapter;
import serializers.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public Gson getGson() {
        return gson;
    }

    protected String parseRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected int getId(String pathString) {
        String[] path = pathString.split("/");
        if (path.length >= 3) {
            return Integer.parseInt(path[2]);
        }
        return -1;
    }

    protected void sendResponse(HttpExchange exchange, Object response, int statusCode) throws IOException {
        try {
            String jsonResponse = gson.toJson(response);
            byte[] bytes = jsonResponse.getBytes(DEFAULT_CHARSET);
            exchange.getResponseHeaders().add("Content-Type", JSON_CONTENT_TYPE);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка при обработке запроса");
        }
    }

    protected void sendTextResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        try {
            byte[] bytes = response.getBytes(DEFAULT_CHARSET);
            exchange.getResponseHeaders().add("Content-Type", JSON_CONTENT_TYPE);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка при обработке запроса");
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendTextResponse(exchange, "Не найдено", 404);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendTextResponse(exchange, "Неверные данные", 400);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendTextResponse(exchange, "Пересечение задач", 406);
    }

    protected void sendInternalError(HttpExchange exchange, String message) throws IOException {
        sendTextResponse(exchange, "Внутренняя ошибка сервера: " + message, 500);
    }

}

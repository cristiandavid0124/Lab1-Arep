import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RestServiceImpl implements RESTService {
    private static List<String> jsonResponses = new ArrayList<>();
    private static int currentId = 1;

    @Override
    public void handleGet(String[] requestLine, BufferedReader in, OutputStream out, Socket clientSocket) throws IOException {
        String combinedJsonResponse = String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 200, combinedJsonResponse);
    }

    @Override
    public void PostRequest(BufferedReader in, OutputStream out) throws IOException {
        String line;
        int contentLength = -1;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        String jugador = extractValue(body, "jugador");
        if (jugador == null) return;

        String jsonResponse = String.format("{ \"id\": %d, \"status\": \"Jugador recibido\", \"jugador\": \"%s\" }", currentId++, jugador);
        jsonResponses.add(jsonResponse);

        String combinedJsonResponse = String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 201, combinedJsonResponse);
    }

    @Override
    public void DeleteRequest(BufferedReader in, OutputStream out, int id) throws IOException {
        List<String> updatedResponses = new ArrayList<>();
        for (String response : jsonResponses) {
            String json = response.trim();
            if (!json.contains("\"id\": " + id)) {
                updatedResponses.add(json);
            }
        }
        jsonResponses = updatedResponses;
        String combinedJsonResponse = String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 200, combinedJsonResponse);
    }

    @Override
    public void PutRequest(BufferedReader in, OutputStream out, int id) throws IOException {
        String line;
        int contentLength = -1;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        String jugador = extractValue(body, "jugador");
        if (jugador == null) return;

        List<String> updatedResponses = new ArrayList<>();
        for (String response : jsonResponses) {
            String json = response.trim();
            if (json.contains("\"id\": " + id)) {
                String updatedJson = String.format("{ \"id\": %d, \"status\": \"Jugador actualizado\", \"jugador\": \"%s\" }", id, jugador);
                updatedResponses.add(updatedJson);
            } else {
                updatedResponses.add(json);
            }
        }
        jsonResponses = updatedResponses;
        String combinedJsonResponse = String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 200, combinedJsonResponse);
    }

    private void sendJsonResponse(OutputStream out, int statusCode, String jsonResponse) throws IOException {
        String statusText;
        switch (statusCode) {
            case 200:
                statusText = "OK";
                break;
            case 201:
                statusText = "Created";
                break;
            case 204:
                statusText = "No Content";
                break;
            case 404:
                statusText = "Not Found";
                break;
            case 400:
                statusText = "Bad Request";
                break;
            default:
                statusText = "OK";
                break;
        }

        String responseHeader = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + jsonResponse.length() + "\r\n" +
                "\r\n";
        out.write(responseHeader.getBytes());
        out.write(jsonResponse.getBytes());
    }

    public static String extractValue(StringBuilder jsonString, String key) {
        int startIndex = jsonString.indexOf("\"" + key + "\":\"");
        if (startIndex == -1) return null;

        startIndex += key.length() + 4;
        int endIndex = jsonString.indexOf("\"", startIndex);
        if (endIndex == -1) return null;

        return jsonString.substring(startIndex, endIndex);
    }
}

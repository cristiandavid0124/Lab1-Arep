import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de los servicios REST para el servidor web.
 * Maneja solicitudes HTTP GET/POST/PUT/DELETE.
 */
public class WebRest implements RESTService {
    private static List<String> jsonResponses = new ArrayList<>();
    private static int currentId = 1;

    @Override
    public void handleGet(String[] requestLine, BufferedReader in, OutputStream out, Socket clientSocket) throws IOException {
        String combinedJsonResponse = jsonResponses.isEmpty() ? "[]" : String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 200, combinedJsonResponse);
    }

    @Override
    public void PostRequest(BufferedReader in, OutputStream out) throws IOException {
        String line;
        int contentLength = -1;

        // Lee las cabeceras para obtener Content-Length
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (contentLength <= 0) {
            send400(out);
            return;
        }

        char[] buffer = new char[contentLength];
        in.read(buffer, 0, contentLength);
        StringBuilder body = new StringBuilder(new String(buffer));

        String jugador = extractValue(body, "jugador");
        if (jugador == null) {
            send400(out);
            return;
        }

        String jsonResponse = String.format("{\"id\": %d, \"status\": \"Jugador recibido\", \"jugador\": \"%s\"}", currentId++, jugador);
        jsonResponses.add(jsonResponse);

        String combinedJsonResponse = String.format("[%s]", String.join(",", jsonResponses));
        sendJsonResponse(out, 201, combinedJsonResponse);
    }

    @Override
    public void DeleteRequest(BufferedReader in, OutputStream out, int id) throws IOException {
        List<String> updatedResponses = new ArrayList<>();
        for (String response : jsonResponses) {
            if (!response.contains("\"id\": " + id)) {
                updatedResponses.add(response);
            }
        }
        jsonResponses = updatedResponses;

        String combinedJsonResponse = jsonResponses.isEmpty() ? "[]" : String.format("[%s]", String.join(",", jsonResponses));
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

        if (contentLength <= 0) {
            send400(out);
            return;
        }

        char[] buffer = new char[contentLength];
        in.read(buffer, 0, contentLength);
        StringBuilder body = new StringBuilder(new String(buffer));

        String jugador = extractValue(body, "jugador");
        if (jugador == null) {
            send400(out);
            return;
        }

        List<String> updatedResponses = new ArrayList<>();
        boolean found = false;
        for (String response : jsonResponses) {
            if (response.contains("\"id\": " + id)) {
                String updatedJson = String.format("{\"id\": %d, \"status\": \"Jugador actualizado\", \"jugador\": \"%s\"}", id, jugador);
                updatedResponses.add(updatedJson);
                found = true;
            } else {
                updatedResponses.add(response);
            }
        }

        // If the ID was not found, respond with 404 Not Found
        if (!found) {
            send404(out);
            return;
        }

        jsonResponses = updatedResponses;

        String combinedJsonResponse = String.format("[%s]", String.join(",", updatedResponses));
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
                "Content-Length: " + jsonResponse.getBytes().length + "\r\n" +
                "\r\n";
        out.write(responseHeader.getBytes());
        out.write(jsonResponse.getBytes());
    }

    private void send400(OutputStream out) throws IOException {
        String responseBody = "{\"status\": \"Error\", \"message\": \"Bad Request\"}";
        sendJsonResponse(out, 400, responseBody);
    }

    private void send404(OutputStream out) throws IOException {
        String responseBody = "{\"status\": \"Error\", \"message\": \"Not Found\"}";
        sendJsonResponse(out, 404, responseBody);
    }

    public static String extractValue(StringBuilder jsonString, String key) {
        String regex = "\"" + key + "\":\"([^\"]*)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(jsonString.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

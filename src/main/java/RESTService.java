import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public interface RESTService {
    void handleGet(String[] requestLine, BufferedReader in, OutputStream out, Socket clientSocket) throws IOException;

    void PostRequest(BufferedReader in, OutputStream out) throws IOException;

    void PutRequest(BufferedReader in, OutputStream out, int id) throws IOException;

    void DeleteRequest(BufferedReader in, OutputStream out, int id) throws IOException;
}

package player.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class HttpUtil {
    private static final String BACKEND_URL = "http://localhost:8080";
    
    public static HttpURLConnection getGetConnection(String endpoint) throws IOException {
        URL url = new URL(BACKEND_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    public static HttpURLConnection getPostConnection(String endpoint) throws IOException {
        URL url = new URL(BACKEND_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        return connection;
    }

    public static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public static String encodeParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }

    public static boolean isSuccessful(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }
}
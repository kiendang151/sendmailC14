package murach.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MailUtilRender {

    public static void sendMail(
            String to,
            String toName,
            String subject,
            String body)
            throws IOException, InterruptedException {

        // Lấy API Key từ biến môi trường
        String apiKey = System.getenv("BREVO_API_KEY");

        System.out.println("===== BREVO TEST =====");
        System.out.println("API KEY EXISTS: "
                + (apiKey != null && !apiKey.isBlank()));

        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException(
                    "BREVO_API_KEY is not configured."
            );
        }

        // Email sender đã đăng ký trên Brevo
        String fromEmail = "kiendang151@gmail.com";

        // Tên người gửi
        String fromName = "Email List Project";

        // Tạo JSON gửi cho Brevo
        String json =
                "{"
                + "\"sender\":{"
                + "\"name\":\""
                + escapeJson(fromName)
                + "\","
                + "\"email\":\""
                + escapeJson(fromEmail)
                + "\""
                + "},"

                + "\"to\":[{"
                + "\"email\":\""
                + escapeJson(to)
                + "\","
                + "\"name\":\""
                + escapeJson(toName)
                + "\""
                + "}],"

                + "\"subject\":\""
                + escapeJson(subject)
                + "\","

                + "\"textContent\":\""
                + escapeJson(body)
                + "\""

                + "}";

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.brevo.com/v3/smtp/email"
                                )
                        )
                        .header(
                                "accept",
                                "application/json"
                        )
                        .header(
                                "api-key",
                                apiKey
                        )
                        .header(
                                "content-type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();
        
        System.out.println("ĐANG GỌI BREVO API...");
        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        // In kết quả để kiểm tra
        System.out.println(
                "BREVO STATUS: "
                + response.statusCode()
        );

        System.out.println(
                "BREVO RESPONSE: "
                + response.body()
        );

        // Nếu Brevo trả lỗi
        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Brevo API error: "
                    + response.statusCode()
                    + " - "
                    + response.body()
            );
        }
    }

    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
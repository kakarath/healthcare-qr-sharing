package com.qr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class QRApp {

    public static void main(String[] args) {
        SpringApplication.run(QRApp.class, args);
    }

    @PostMapping("/api/qr/generate")
    public Map<String, Object> generateQR(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", "session-" + System.currentTimeMillis());
        response.put("qrCodeImage", "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
        response.put("expiresAt", "2025-09-03T19:00:00");
        response.put("success", true);
        response.put("message", "QR Generated Successfully!");
        return response;
    }

    @GetMapping("/")
    public String home() {
        return """
            <!DOCTYPE html>
            <html>
            <head><title>QR Generator</title></head>
            <body>
                <h1>QR Code Generator</h1>
                <button onclick="generateQR()">Generate QR Code</button>
                <div id="result"></div>
                <script>
                async function generateQR() {
                    try {
                        const response = await fetch('/api/qr/generate', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify({test: 'data'})
                        });
                        const result = await response.json();
                        document.getElementById('result').innerHTML = 
                            '<h3>Success!</h3><pre>' + JSON.stringify(result, null, 2) + '</pre>';
                    } catch (error) {
                        document.getElementById('result').innerHTML = 
                            '<h3>Error:</h3>' + error.message;
                    }
                }
                </script>
            </body>
            </html>
            """;
    }
}
package com.conversor.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private final OkHttpClient client;

    // Constructor con configuración
    public HttpClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  // Timeout de conexión
                .readTimeout(30, TimeUnit.SECONDS)     // Timeout de lectura
                .writeTimeout(10, TimeUnit.SECONDS)    // Timeout de escritura
                .build();
    }

    // Método para hacer GET requests
    public String get(String url) throws IOException {
        // Crear request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")  // Esperamos JSON
                .addHeader("User-Agent", "ConversorMonedas/1.0")
                .build();

        // Ejecutar request y obtener response
        try (Response response = client.newCall(request).execute()) {
            // Verificar si la respuesta fue exitosa
            if (!response.isSuccessful()) {
                throw new IOException("Error en la solicitud: Código " +
                        response.code() + " - " + response.message());
            }

            // Verificar que haya cuerpo en la respuesta
            if (response.body() == null) {
                throw new IOException("Respuesta vacía del servidor");
            }

            // Leer y retornar el cuerpo como String
            return response.body().string();
        }
    }

    // Método para verificar conexión
    public boolean testConnection(String url) {
        try {
            get(url);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

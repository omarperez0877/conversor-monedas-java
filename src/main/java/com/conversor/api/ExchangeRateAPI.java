package com.conversor.api;

import com.conversor.config.ConfigManager;
import com.conversor.models.ExchangeRateResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import com.conversor.api.exceptions.ApiException;

public class ExchangeRateAPI {
    private final HttpClient httpClient;
    private final Gson gson;
    private final ConfigManager config;

    public ExchangeRateAPI() {
        this.httpClient = new HttpClient();
        this.gson = new Gson();
        this.config = ConfigManager.getInstance();

        // Verificar API key
        String apiKey = config.getApiKey();
        if ("demo".equals(apiKey)) {
            System.out.println("⚠️  MODO DEMO ACTIVADO - Usando API key de demostración");
            System.out.println("🔗 Para funcionalidad completa, configura tu API key en api.properties");
        }
    }

    public ExchangeRateResponse getLatestRates() throws IOException, ApiException {
        String currency = config.getBaseCurrency();
        String url = config.getLatestRatesUrl(currency);

        System.out.println("🔗 Endpoint: " + url.replace(config.getApiKey(), "***"));

        return executeRequest(url);
    }

    public ExchangeRateResponse getLatestRates(String baseCurrency)
            throws IOException, ApiException {
        String url = config.getBaseUrl() + config.getApiKey() + "/" +
                config.getLatestRatesEndpoint(baseCurrency);

        return executeRequest(url);
    }

    public PairConversionResponse convertPair(String from, String to, double amount)
            throws IOException, ApiException {
        String url = config.getBaseUrl() + config.getApiKey() + "/" +
                config.getPairConversionEndpoint(from, to) + "/" + amount;

        String jsonResponse = httpClient.get(url);
        return gson.fromJson(jsonResponse, PairConversionResponse.class);
    }

    private ExchangeRateResponse executeRequest(String url)
            throws IOException, ApiException {
        try {
            String jsonResponse = httpClient.get(url);
            ExchangeRateResponse response = gson.fromJson(jsonResponse,
                    ExchangeRateResponse.class);

            if (!response.isSuccess()) {
                throw new ApiException("Error de API: " + response.getResult());
            }

            return response;

        } catch (JsonSyntaxException e) {
            throw new ApiException("Error parseando JSON: " + e.getMessage());
        }
    }

    // Clase para respuesta de conversión directa
    public static class PairConversionResponse {
        private String result;
        private String documentation;
        private String terms_of_use;
        private long time_last_update_unix;
        private String time_last_update_utc;
        private String base_code;
        private String target_code;
        private double conversion_rate;
        private double conversion_result;

        // Getters y setters
        public double getConversionResult() {
            return conversion_result;
        }

        public double getConversionRate() {
            return conversion_rate;
        }
    }
}
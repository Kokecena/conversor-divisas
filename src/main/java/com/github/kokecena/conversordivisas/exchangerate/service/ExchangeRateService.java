package com.github.kokecena.conversordivisas.exchangerate.service;

import com.github.kokecena.conversordivisas.exchangerate.model.ExchangeRateCodes;
import com.github.kokecena.conversordivisas.exchangerate.model.ExchangeRateResponse;
import com.google.gson.*;
import io.avaje.config.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExchangeRateService {

    private static final String EXCHANGE_RATE_URL = "https://v6.exchangerate-api.com/v6/".concat(Config.get("service.exchange-rate.api-key"));
    private final Gson gson;

    public ExchangeRateService() {
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (jsonElement, type, jsonDeserializationContext) -> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                            return LocalDateTime.parse(jsonElement.getAsString(), formatter);
                        })
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public CompletableFuture<ExchangeRateResponse> getExchangeFrom(String baseCode, Executor executor) {
        String requestUrl = EXCHANGE_RATE_URL.concat("/latest/").concat(baseCode);
        return HttpClient.newHttpClient()
                .sendAsync(HttpRequest.newBuilder()
                        .uri(URI.create(requestUrl))
                        .build(), HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body, executor)
                .thenApplyAsync(s -> gson.fromJson(s, ExchangeRateResponse.class))
                .toCompletableFuture();

    }

    public CompletableFuture<ExchangeRateCodes> getSupportedCodes(Executor executor) {
        String requestUrl = EXCHANGE_RATE_URL.concat("codes");
        return HttpClient.newHttpClient()
                .sendAsync(HttpRequest.newBuilder()
                        .uri(URI.create(requestUrl))
                        .build(), HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body, executor)
                .thenApplyAsync(s -> gson.fromJson(s, ExchangeRateCodes.class))
                .toCompletableFuture();
    }
}
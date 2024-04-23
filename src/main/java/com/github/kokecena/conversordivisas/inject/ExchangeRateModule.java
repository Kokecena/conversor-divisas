package com.github.kokecena.conversordivisas.inject;

import com.github.kokecena.conversordivisas.controller.ExchangeController;
import com.github.kokecena.conversordivisas.exchangerate.service.ExchangeRateService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Factory
public class ExchangeRateModule {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    @Bean
    public ExchangeRateService getExchangeRateService() {
        return new ExchangeRateService(new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (jsonElement, type, jsonDeserializationContext) ->
                                LocalDateTime.parse(jsonElement.getAsString(), formatter))
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create());
    }

    @Bean
    public ExchangeController exchangeController(ExchangeRateService service) {
        return new ExchangeController(service);
    }

}

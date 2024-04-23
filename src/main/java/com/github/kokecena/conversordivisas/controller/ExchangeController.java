package com.github.kokecena.conversordivisas.controller;

import com.github.kokecena.conversordivisas.exchangerate.model.ExchangeRateCodes;
import com.github.kokecena.conversordivisas.exchangerate.service.ExchangeRateService;
import io.github.parubok.swingfx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExchangeController {

    private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);

    private final Map<String, Double> conversionRates = new HashMap<>();
    private boolean exchangeRatesUpdated;
    private final ExchangeRateService exchangeRateService;
    private StringProperty toCurrencyCode;
    private ReadOnlyObjectWrapper<LocalDateTime> lastUpdate;
    private ReadOnlyDoubleWrapper currentValue;
    private String baseCode;

    public ExchangeController(ExchangeRateService service) {
        exchangeRateService = service;
        toCurrencyCodeProperty().addListener((o, oldValue, newValue) -> log.info("Change currency code from {} to {}", oldValue, newValue));
    }

    public CompletableFuture<Void> updateExchangeRatesFrom(String baseCode, Executor executor) {
        this.baseCode = baseCode;
        return exchangeRateService.getExchangeFrom(baseCode, executor)
                .thenAccept(exchangeRateResponse -> {
                    conversionRates.clear();
                    conversionRates.putAll(exchangeRateResponse.conversionRates());
                    setLastUpdate(exchangeRateResponse.timeLastUpdateUtc());
                    exchangeRatesUpdated = true;
                }).exceptionally(throwable -> {
                    log.error("On exchange rates update failed, retrieve USD", throwable);
                    return null;
                });
    }

    public CompletableFuture<Map<String, String>> getSupportedCodes(Executor executor) {
        return exchangeRateService.getSupportedCodes(executor)
                .thenApply(ExchangeRateCodes::supportedCodes);
    }

    public double getCurrencyValue(String code) {
        if (getToCurrencyCode() != null && getToCurrencyCode().equalsIgnoreCase(code)) {
            if (exchangeRatesUpdated) {
                log.info("Updating base exchange rates from {}", baseCode);
                setCurrentValue(conversionRates.getOrDefault(code.toUpperCase(), 0.0d));
                exchangeRatesUpdated = false;
            }
            getCurrentValue();
        }
        setToCurrencyCode(code);
        return getCurrentValue();
    }

    public String getToCurrencyCode() {
        return toCurrencyCode == null ? null : toCurrencyCode.get();
    }

    public void setToCurrencyCode(String toCurrencyCode) {
        toCurrencyCodeProperty().set(toCurrencyCode);
    }

    public StringProperty toCurrencyCodeProperty() {
        if (toCurrencyCode == null) {
            toCurrencyCode = new SimpleStringProperty() {
                @Override
                protected void invalidated() {
                    String code = get().toUpperCase();
                    setCurrentValue(conversionRates.getOrDefault(code, 0.0));
                }
            };
        }
        return toCurrencyCode;
    }

    public double getCurrentValue() {
        return currentValue == null ? 0.0 : currentValue.getValue();
    }


    private void setCurrentValue(double currentValue) {
        currentValuePropertyImpl().set(currentValue);
    }

    public ReadOnlyDoubleProperty currentValueProperty() {
        return currentValuePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper currentValuePropertyImpl() {
        if (currentValue == null) {
            currentValue = new ReadOnlyDoubleWrapper();
        }
        return currentValue;
    }

    private void setLastUpdate(LocalDateTime lastUpdate) {
        lastUpdatePropertyImpl().set(lastUpdate);
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate == null ? null : lastUpdate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastUpdateProperty() {
        return lastUpdatePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<LocalDateTime> lastUpdatePropertyImpl() {
        if (lastUpdate == null) {
            lastUpdate = new ReadOnlyObjectWrapper<>();
        }
        return lastUpdate;
    }

}
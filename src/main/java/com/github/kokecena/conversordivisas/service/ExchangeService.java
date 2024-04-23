package com.github.kokecena.conversordivisas.service;

import com.github.kokecena.conversordivisas.exchangerate.service.ExchangeRateService;
import io.github.parubok.swingfx.beans.property.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExchangeService {

    private final Map<String, Double> conversionRates = new HashMap<>();
    private boolean exchangeRatesUpdated;
    private final ExchangeRateService exchangeRateService;
    private StringProperty baseCurrencyCode;
    private ReadOnlyObjectWrapper<LocalDateTime> lastUpdate;
    private ReadOnlyDoubleWrapper currentValue;

    public ExchangeService(ExchangeRateService service) {
        exchangeRateService = service;
    }

    public CompletableFuture<Void> updateExchangeRatesFrom(String code, Executor executor) {
        return exchangeRateService.getExchangeFrom(code, executor)
                .thenAccept(exchangeRateResponse -> {
                    conversionRates.clear();
                    conversionRates.putAll(exchangeRateResponse.conversionRates());
                    setLastUpdate(exchangeRateResponse.timeLastUpdateUtc());
                    exchangeRatesUpdated = true;
                });
    }

    public double getCurrencyValue(String code) {
        if (getBaseCurrencyCode() != null && getBaseCurrencyCode().equalsIgnoreCase(code)) {
            if (exchangeRatesUpdated) {
                setCurrentValue(conversionRates.getOrDefault(code.toUpperCase(), 0.0d));
                exchangeRatesUpdated = false;
            }
            getCurrentValue();
        }
        setBaseCurrencyCode(code);
        return getCurrentValue();
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode == null ? null : baseCurrencyCode.get();
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        baseCurrencyCodeProperty().set(baseCurrencyCode);
    }

    public StringProperty baseCurrencyCodeProperty() {
        if (baseCurrencyCode == null) {
            baseCurrencyCode = new SimpleStringProperty() {
                @Override
                protected void invalidated() {
                    setCurrentValue(conversionRates.getOrDefault(get().toUpperCase(), 0.0d));
                }
            };
        }
        return baseCurrencyCode;
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
package com.github.kokecena.conversordivisas.exchangerate.model;

import java.time.LocalDateTime;
import java.util.Map;

public record ExchangeRateResponse(String result,
                                   String baseCode,
                                   LocalDateTime timeLastUpdateUtc,
                                   Map<String, Double> conversionRates) {
}

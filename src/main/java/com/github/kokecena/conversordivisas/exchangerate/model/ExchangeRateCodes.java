package com.github.kokecena.conversordivisas.exchangerate.model;

import java.util.Map;

public record ExchangeRateCodes(String result, Map<String,String> supportedCodes) {
}

package com.github.kokecena.conversordivisas.components.combobox;

import com.github.kokecena.conversordivisas.components.combobox.model.Currency;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CurrencyComboBox extends JComboBox<Currency> {

    private List<Currency> currencies;
    private DefaultComboBoxModel<Currency> codesComboBoxModel;
    private Supplier<Currency> DEFAULT_CURRENCY = () -> new Currency("USD", "United States Dollar");

    public void setCurrency(String code) {
        if (currencies == null || currencies.isEmpty()) {
            throw new IllegalStateException("Model not initialized!");
        }
        codesComboBoxModel.setSelectedItem(currencies.stream()
                .filter(currency -> currency.code().equalsIgnoreCase(code))
                .findFirst()
                .orElseGet(DEFAULT_CURRENCY));
    }

    public Optional<Currency> getSelectedCurrency() {
        if (currencies == null || currencies.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable((Currency) codesComboBoxModel.getSelectedItem());
    }

    public Currency getCurrentCurrency() {
        if (currencies == null || currencies.isEmpty()) {
            throw new IllegalStateException("Model not initialized!");
        }
        return (Currency) codesComboBoxModel.getSelectedItem();
    }

    public void setModel(Map<String, String> codes) {
        currencies = codes.entrySet()
                .stream()
                .map(stringStringEntry -> new Currency(stringStringEntry.getKey(), stringStringEntry.getValue()))
                .toList();
        codesComboBoxModel = new DefaultComboBoxModel<>() {
            @Override
            public int getSize() {
                return currencies.size();
            }

            @Override
            public Currency getElementAt(int index) {
                return currencies.get(index);
            }
        };
        setModel(codesComboBoxModel);
    }

}
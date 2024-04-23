package com.github.kokecena.conversordivisas.components.combobox.model;

public record Currency(String code, String name) {
    @Override
    public String toString() {
        return code + " - " + name;
    }
}
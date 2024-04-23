package com.github.kokecena.conversordivisas.application;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import io.avaje.inject.BeanScope;

import javax.swing.SwingUtilities;

public class Launch {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatMacDarkLaf.setup();
            try (BeanScope scope = BeanScope.builder().shutdownHook(true).build()) {
                ApplicationStarter app = scope.get(ApplicationStarter.class);
                app.start();
            }
        });
    }

}
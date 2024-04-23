package com.github.kokecena.conversordivisas;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.github.kokecena.conversordivisas.exchangerate.service.ExchangeRateService;
import com.github.kokecena.conversordivisas.view.MainView;

import javax.swing.SwingUtilities;

public class Launch {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatMacDarkLaf.setup();
            MainView mainView = new MainView(new ExchangeRateService());
            mainView.startApp();
        });
    }

}
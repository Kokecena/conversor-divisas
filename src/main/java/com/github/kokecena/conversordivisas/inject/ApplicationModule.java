package com.github.kokecena.conversordivisas.inject;

import com.github.kokecena.conversordivisas.controller.ExchangeController;
import com.github.kokecena.conversordivisas.view.MainView;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

@Factory
public class ApplicationModule {

    @Bean
    public MainView mainView(ExchangeController controller) {
        return new MainView(controller);
    }

}

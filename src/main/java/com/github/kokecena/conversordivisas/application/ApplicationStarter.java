package com.github.kokecena.conversordivisas.application;

import com.github.kokecena.conversordivisas.view.MainView;
import io.avaje.inject.Component;
import jakarta.inject.Inject;

@Component
public class ApplicationStarter {

    private final MainView view;

    @Inject
    public ApplicationStarter(MainView view) {
        this.view = view;
    }

    public void start() {
        view.init();
    }

}

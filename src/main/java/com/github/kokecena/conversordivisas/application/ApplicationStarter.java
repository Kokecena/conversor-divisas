package com.github.kokecena.conversordivisas.application;

import com.github.kokecena.conversordivisas.view.MainView;
import io.avaje.inject.Component;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);

    private final MainView view;

    @Inject
    public ApplicationStarter(MainView view) {
        this.view = view;
    }

    public void start() {
        log.info("Application started");
        view.init();
    }

}

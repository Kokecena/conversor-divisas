package com.github.kokecena.conversordivisas.commons;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class KLFactory {

    private KLFactory() {

    }

    public static KeyListener onKeyPressed(Consumer<KeyEvent> action) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                action.accept(e);
            }
        };
    }

    public static KeyListener onKeyTyped(Consumer<KeyEvent> action) {
        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                action.accept(e);
            }
        };
    }

}
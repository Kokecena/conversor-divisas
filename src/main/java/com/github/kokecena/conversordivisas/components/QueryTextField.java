package com.github.kokecena.conversordivisas.components;

import io.github.parubok.swingfx.beans.property.ReadOnlyStringProperty;
import io.github.parubok.swingfx.beans.property.ReadOnlyStringWrapper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class QueryTextField extends JTextField {
    private ReadOnlyStringWrapper query;

    public QueryTextField() {
        DocumentListener searchListener = new DocumentListener() {
            public void changeFilter(DocumentEvent event) {
                Document document = event.getDocument();
                try {
                    setQuery(document.getText(0, document.getLength()));
                } catch (BadLocationException ignored) {
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeFilter(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                changeFilter(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeFilter(e);
            }
        };
        getDocument().addDocumentListener(searchListener);
    }

    public String getQuery() {
        return query == null ? "" : query.get();
    }

    private void setQuery(String text) {
        queryPropertyImpl().set(text);
    }

    public ReadOnlyStringProperty queryProperty() {
        return queryPropertyImpl().getReadOnlyProperty();
    }


    private ReadOnlyStringWrapper queryPropertyImpl() {
        if (query == null) {
            query = new ReadOnlyStringWrapper(this, "query", "");
        }
        return query;
    }
}

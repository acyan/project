/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author dasha
 */
public class Message {
    private StringProperty message;
    private StringProperty date;
    
    public final void setMessage(String value) {
        messageProperty().set(value);
    }

    public final String getMessage() {
        return messageProperty().get();
    }

    public StringProperty messageProperty() {
        if (message == null) {
            message = new SimpleStringProperty();
        }
        return message;
    }
    
    public final void setDate(String value) {
        dateProperty().set(value);
    }

    public final String getDate() {
        return dateProperty().get();
    }

    public StringProperty dateProperty() {
        if (date == null) {
            date = new SimpleStringProperty();
        }
        return date;
    }

    public Message(String message, String date) {
        setMessage(message);
        setDate(date);
    }

    
}

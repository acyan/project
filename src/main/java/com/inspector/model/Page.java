/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Даша
 */
public class Page {
    private StringProperty name;
    private StringProperty status;
    private StringProperty oldSum;
    private StringProperty newSum;
    private StringProperty title;
    
    public final void setName(String value) {
        nameProperty().set(value);
    }

    public final String getName() {
        return nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }
    
    public final void setStatus(String value) {
        statusProperty().set(value);
    }

    public final String getStatus() {
        return statusProperty().get();
    }

    public StringProperty statusProperty() {
        if (status == null) {
            status = new SimpleStringProperty();
        }
        return status;
    }
    
    public final void setNewSum(String value) {
        newSumProperty().set(value);
    }

    public final String getNewSum() {
        return newSumProperty().get();
    }

    public StringProperty newSumProperty() {
        if (newSum == null) {
            newSum = new SimpleStringProperty();
        }
        return newSum;
    }

    public final void setOldSum(String value) {
        oldSumProperty().set(value);
    }

    public final String getOldSum() {
        return oldSumProperty().get();
    }

    public StringProperty oldSumProperty() {
        if (oldSum == null) {
            oldSum = new SimpleStringProperty();
        }
        return oldSum;
    }

    public final void setTitle(String value) {
        titleProperty().set(value);
    }

    public final String getTitle() {
        return titleProperty().get();
    }

    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty();
        }
        return title;
    }   
    public Page(String name) {
        setName(name);
        setStatus(null);
        setNewSum("0");
        setOldSum("0");
        setTitle("");
    }
    
    
}

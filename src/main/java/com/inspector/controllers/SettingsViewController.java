/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.controllers;

import com.inspector.MainApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Даша
 */
public class SettingsViewController {

    private Stage dialogStage;
    private boolean okClicked = false;
    @FXML
    private ChoiceBox statusSecChoiceBox;
    @FXML
    private ChoiceBox statusMinChoiceBox;
    @FXML
    private ChoiceBox statusHourChoiceBox;
    @FXML
    private ChoiceBox changesChoiceBox;   
    @FXML
    private TextField statusSecField;
    @FXML
    private TextField statusMinField;
    @FXML
    private TextField statusHourField;
    @FXML
    private TextField changeField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private CheckBox proxyCheckBox;
    
    private MainApp mainApp;
    
    @FXML
    public void initialize() {
        ObservableList<Integer> seconds = FXCollections.observableArrayList();
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        for(int i = 0;i<61;i++){
            seconds.add(i);
            minutes.add(i);
        }
        for(int i=0;i<25;i++){
            hours.add(i);
        }
        statusSecChoiceBox.setItems(seconds);
        statusMinChoiceBox.setItems(minutes);
        statusHourChoiceBox.setItems(hours);
        
        proxyCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                portField.setDisable(!newValue);
                ipField.setDisable(!newValue);
            }
        });
        
//        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                mainApp.getPreferences().getUserPrefs().putInt("item", Integer.parseInt(newValue.toString()));
//                
//            }
//        });
    }    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    @FXML
    private void handleOk() {
        int microSeconds = (int)statusSecChoiceBox.getValue()*1000+(int)statusMinChoiceBox.getValue()*60*1000+(int)statusHourChoiceBox.getValue()*60*60*1000;
    //    int microSeconds = Integer.parseInt(statusSecField.getText())*1000+Integer.parseInt(statusMinField.getText())*60000+Integer.parseInt(statusHourField.getText())*1000*60*60;
        mainApp.getPreferences().setStatusFrequency(String.valueOf(microSeconds));
        mainApp.getPreferences().setProxy(proxyCheckBox.isSelected());
        mainApp.getPreferences().setProxyAddress(ipField.getText());
        mainApp.getPreferences().setProxyPort(Integer.parseInt(portField.getText()));
        
        okClicked = true;
        dialogStage.close();
        
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        int hours = Integer.parseInt(mainApp.getPreferences().getStatusFrequency())/1000/60/60;
        int minutes = (Integer.parseInt(mainApp.getPreferences().getStatusFrequency())-hours*60*60*1000)/60/1000;
        int seconds = (Integer.parseInt(mainApp.getPreferences().getStatusFrequency())-hours*60*60*1000-minutes*60*1000)/1000;
        statusSecChoiceBox.setValue(seconds);
        statusMinChoiceBox.setValue(minutes);
        statusHourChoiceBox.setValue(hours);
        
        proxyCheckBox.setSelected(mainApp.getPreferences().getProxy());
        ipField.setText(mainApp.getPreferences().getProxyAddress());
        portField.setText(mainApp.getPreferences().getProxyPort().toString());
        ipField.setDisable(!proxyCheckBox.isSelected());
        portField.setDisable(!proxyCheckBox.isSelected());
//        statusSecField.setText(String.valueOf(seconds));
//        statusMinField.setText(String.valueOf(minutes));
//        statusHourField.setText(String.valueOf(hours));
    }
    
    public void setSettings(){
        
    }
    public boolean isOkClicked() {
        return okClicked;
    }
}

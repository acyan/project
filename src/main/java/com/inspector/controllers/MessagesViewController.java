/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.controllers;

import com.inspector.MainApp;
import com.inspector.model.Message;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author dasha
 */

public class MessagesViewController{
    @FXML
    private TableView<Message> messageTable;
    @FXML
    private TableColumn<Message, String> messageColumn;
    @FXML
    private TableColumn<Message, String> dateColumn;
    
    private MainApp mainApp;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        messageColumn.setCellValueFactory(cellData->cellData.getValue().messageProperty());
        dateColumn.setCellValueFactory(cellData->cellData.getValue().dateProperty());
        
        messageTable.getItems().addListener((ListChangeListener.Change<? extends Message> c) -> {
            c.next();
            final int size = messageTable.getItems().size();
            if (size > 0) {
                messageTable.scrollTo(size - 1);
            }
        });
        
    }    
    
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        messageTable.setItems(mainApp.getMessages());
        messageTable.getItems().addListener(new ListChangeListener<Message>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Message> c) {
                c.next();
                if(messageTable.getItems().size()>20){
                    messageTable.getItems().remove(0);
                }
                if(c.wasAdded()){
                    messageTable.scrollTo(messageTable.getItems().size());
                }
            }
        });
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.controllers;

import com.inspector.MainApp;
import com.inspector.model.Page;
import com.inspector.model.Site;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Даша
 */
public class SiteEditDialogController {

    @FXML
    private TextField addressField;
    @FXML
    private RadioButton yesRadioButton;
    @FXML
    private RadioButton noRadioButton;
    @FXML
    private TableView<Page> pagesList;
    @FXML
    private TableColumn<Page, String> pageAddressColumn;
    @FXML
    private TableColumn<Page, String> pageStatusColumn;
    @FXML
    private TextField pageName;
    
    private MainApp mainApp;
    private Stage dialogStage;
    private Site site;
    private ObservableList<Page> pages;
    
    private boolean okClicked = false;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        pageAddressColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
    }    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    } 
    
    public void setSite(Site site,MainApp mainApp){
        this.site=site;
        this.mainApp = mainApp;
        
        addressField.setText(site.getName());
        if(site.getChange()){
            yesRadioButton.setSelected(true);
        }else{
            noRadioButton.setSelected(true);
        }
        pages = FXCollections.observableList(new ArrayList<>(site.pagesProperty()));
        pagesList.setItems(pages);
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleAdd(){
        pagesList.getItems().add(new Page(pageName.getText()));
        
        pageName.setText("");
    }
    
    @FXML
    private void handleDelete(){
            int selectedIndex = pagesList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                pages.remove(selectedIndex);       
            } else {
                    // Nothing selected.
                    Dialogs.create()
                    .title("No Selection")
                    .masthead("No Person Selected")
                    .message("Please select a person in the table.")
                    .showWarning();
            }
    }
    @FXML
    private void handleOk() {
            site.setName(addressField.getText());
            site.setChange(yesRadioButton.isSelected());
            site.setPages(pages);                    
            mainApp.getChangesService().setSites(new ArrayList<>(mainApp.getSites()));
            
            okClicked = true;
            dialogStage.close();
        
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}

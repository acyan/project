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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.dialog.Dialogs;
/**
 * FXML Controller class
 *
 * @author dasha
 */
public class SiteOverviewController{
    
    @FXML
    private TableView<Site> siteTable;
    @FXML
    private TableColumn<Site, String> addressColumn;
    @FXML
    private TableColumn<Site, String> statusColumn;
    @FXML
    private Label addressLabel;
    @FXML
    private Label changesLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<Page> pagesList;
    @FXML
    private TableColumn<Page, String> pageAddressColumn;
    @FXML
    private TableColumn<Page, String> pageStatusColumn;
    @FXML
    private Label labelTest;
    
    private MainApp mainApp;

    private SimpleStringProperty value;
    
    public SiteOverviewController() {
    }
 
    
    @FXML
    public void initialize() {
        
        addressColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        
        pageAddressColumn.setCellValueFactory(data->data.getValue().nameProperty());
        pageStatusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        
        showSiteDetails(null);
        
        siteTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Site> observable, Site oldValue, Site newValue) -> showSiteDetails(newValue));
        
    }    
    
    public void setMainApp(MainApp mainApp){
        this.mainApp=mainApp;
        
        siteTable.setItems(mainApp.getSites());      
 //       labelTest.textProperty().bindBidirectional(mainApp.timeProperty());
    }
    private void showSiteDetails(Site site) {
    	if (site != null) {
            addressLabel.setText(site.getName());
            statusLabel.setText(site.getStatus());
            pagesList.setItems(site.pagesProperty());
            if(site.getChange())
                changesLabel.setText("Да");
            else {
                changesLabel.setText("Нет");
            }
            
    	} else {
            addressLabel.setText("");
            changesLabel.setText("");
            statusLabel.setText("");
            pagesList.setItems(null);
    	}
    }
    
    @FXML
    private void handleDeleteSite() {
            int selectedIndex = siteTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                siteTable.getItems().remove(selectedIndex);
                mainApp.getStatusService().setSites(mainApp.getUrl(mainApp.getSites()));
            //    mainApp.getSites().forEach(h->System.out.println(h.getName()));
            mainApp.getChangesService().setSites(new ArrayList<>(mainApp.getSites()));
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
    private void handleNewSite() {
            Site temp = new Site();
            boolean okClicked = mainApp.showSiteEditDialog(temp);
            if (okClicked) {
                mainApp.getSites().add(temp);
            //    mainApp.getSites().forEach(f->System.out.println(f.getName()));
                mainApp.getStatusService().getSites().add(temp.getName());
                mainApp.getChangesService().setSites(new ArrayList<>(mainApp.getSites()));          
            }
    }  
    
    @FXML
    private void handleEditSite() {
            Site selectedItem = siteTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                    boolean okClicked = mainApp.showSiteEditDialog(selectedItem);
                    if (okClicked) {
                            showSiteDetails(selectedItem);
                    }

            } else {
                    Dialogs.create()
                            .title("No Selection")
                            .masthead("No Person Selected")
                            .message("Please select a person in the table.")
                            .showWarning();
            }
    }
    @FXML
    private void handleTest() {
        labelTest.setText(mainApp.getChangesService().getState().toString());
    }
}

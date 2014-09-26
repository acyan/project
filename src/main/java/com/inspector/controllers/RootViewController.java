package com.inspector.controllers;

import com.inspector.MainApp;
import java.io.File;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.Dialogs;


/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Marco Jakob
 */
public class RootViewController {

    // Reference to the main application
    private MainApp mainApp;

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Creates an empty address book.
     */
    @FXML
    private void handleNew() {
        mainApp.getSites().clear();
        mainApp.setFilePath(null);
    }

    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadData();
        }
    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave() {
//        File personFile = mainApp.getFilePath();
//        if (personFile != null) {
//            mainApp.saveData(personFile);
//        } else {
//            handleSaveAs();
//        }
        handleSaveAs();
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			mainApp.saveData();
		}
	}

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
		Dialogs.create()
	        .title("AddressApp")
	        .masthead("About")
	        .message("Author: Marco Jakob\nWebsite: http://code.makery.ch")
	        .showInformation();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    @FXML
    private void handleSettings(){
        mainApp.showSettingsView();
    }
    
    @FXML
    private void handleStatistics(){
        mainApp.showStatistics();
    }
    
    @FXML
    private void handleReport(){
        mainApp.createReport();
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector;

import com.inspector.controllers.MessagesViewController;
import com.inspector.controllers.RootViewController;
import com.inspector.controllers.SettingsViewController;
import com.inspector.controllers.SiteEditDialogController;
import com.inspector.controllers.SiteOverviewController;
import com.inspector.controllers.StatisticsViewController;
import com.inspector.model.ChangesService2;
import com.inspector.model.Message;
import com.inspector.model.Page;
import com.inspector.model.Site;
import com.inspector.model.StatusService;
import com.inspector.model.UserPreferences;
import com.inspector.util.DBAdapter;
import com.inspector.util.FileUtil;
import com.inspector.util.SiteWrapper;
import com.inspector.util.Status;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author dasha
 */
public class MainApp extends Application{

    private Stage primaryStage;
    private BorderPane rootLayout;
    private TabPane tabPane;

    
    private ObservableList<Site> siteData = FXCollections.observableArrayList();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    
    private StatusService statusService;
    private UserPreferences pref;
    private ChangesService2 changesService;
    private DBAdapter adapter;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Приложение");   
        this.primaryStage.setOnCloseRequest((WindowEvent event) -> {
            saveData();
        });
        
        initRootLayout();
        showSiteOverview();
        showMessagesView();
    }

    public MainApp() {
        Site newSite = new Site("http://yandex.ru", Boolean.TRUE);
        newSite.pagesProperty().add(new Page("http://yandex.ru"));
        newSite.pagesProperty().add(new Page("http://market.yandex.ru"));
      //  siteData.add(new Site("http://google.com", Boolean.TRUE));
        siteData.add(newSite);
        loadData();
        adapter = new DBAdapter(getPages());
        
        pref=new UserPreferences();
        pref.statusFrequencyProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(Integer.parseInt(newValue)>0){
                
                statusService.setPeriod(new Duration(Integer.parseInt(newValue)));  
                if(!statusService.isRunning()){
                    if(statusService.getState()==Worker.State.CANCELLED)
                        statusService.restart();
                    else
                        statusService.start();
                    
                     System.out.println("сервис перезапущен");
                }
            } else{
                statusService.cancel();
                System.out.println("сервис завершен");
            }

        });
        
        this.statusService = new StatusService(getUrl(siteData),pref);
        statusService.setDelay(new Duration(300));
        statusService.setPeriod(new Duration(Integer.parseInt(pref.getStatusFrequency())));  
        statusService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        BlockingQueue<String> results = null;
            @Override
            public void handle(WorkerStateEvent event) {
                results = (BlockingQueue<String>) event.getSource().getValue();
                siteData.forEach((site) -> {
                    if(!site.getStatus().equals(results.peek())){
                        if(results.peek().equals(Status.ACTIVE.getValue()))
                            addMessage("Сайт "+site.getName()+" доступен");
                        else {
                            addMessage("Сайт "+site.getName()+" не доступен");
                        }
                    }
                    site.setStatus(results.poll());
            });
                
            }
        });
        if(!statusService.getPeriod().lessThan(Duration.ONE))
            statusService.start();
        
        this.changesService = new ChangesService2(new ArrayList<>(siteData), pref);
        changesService.setDelay(new Duration(3000));
        changesService.setPeriod(Duration.minutes(1));
        
        changesService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            CopyOnWriteArrayList<Site> results = null;
            @Override
            public void handle(WorkerStateEvent event) {
                results = (CopyOnWriteArrayList<Site>) event.getSource().getValue();
                CopyOnWriteArrayList<Site> temp = new CopyOnWriteArrayList<Site>();
                if(results.size()!=siteData.size()){
                    
                }
                System.out.println(changesService.getState());
                siteData.clear();
                siteData.addAll(results);               
                siteData.forEach(site->{
                        site.getPages().forEach(page->{
                            
                             if((!page.getOldSum().equals(page.getNewSum())&&(!page.getOldSum().equals("0")))){
                                adapter.insertDate(page.getName());
                          //      adapter.getCountAll(page.getName());
                                addMessage("Произошли изменения на странице "+page.getName());
                                page.setStatus("yes");
                            } else{
                                page.setStatus("no");
                            }
                        });
                    
                });
            }
        });
        
        System.out.println(changesService.getState());
        changesService.start();
        System.out.println(changesService.getState());
//        for(Site site: siteData){
//            site.changeProperty().addListener(new ChangeListener<Boolean>() {
//
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//
//                    changesService.setSites(getPages());
//                        
//                }
//            });
//            site.getPages().addListener(new ListChangeListener<Page>() {
//
//                @Override
//                public void onChanged(ListChangeListener.Change<? extends Page> c) {
//                    
//                    changesService.setSites(getPages());
//                }
//            });
//        }

//        time = new SimpleStringProperty("0");
//        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                int val = Integer.parseInt(time.getValue());
//                val++;
//                time.setValue(new Integer(val).toString());
//            }
//        }));
//        timer.setCycleCount(Timeline.INDEFINITE);
//        timer.play();       
    }
    
public void initRootLayout() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class
                .getResource("/fxml/RootView.fxml"));
        rootLayout = (BorderPane) loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        RootViewController controller = loader.getController();
        controller.setMainApp(this);

        primaryStage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    
    public void showSiteOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/SiteOverview.fxml"));
            AnchorPane siteOverview = (AnchorPane) loader.load();
            tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Tab sitesTab = new Tab("Сайты");
            sitesTab.setContent(siteOverview);
            tabPane.getTabs().addAll(sitesTab);
            
            rootLayout.setCenter(tabPane);
            
            SiteOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessagesView() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/MessagesView.fxml"));
            AnchorPane messagesView = (AnchorPane) loader.load();
            
            Tab messagesTab = new Tab("Уведомления");
            messagesTab.setContent(messagesView);
            tabPane.getTabs().add(messagesTab);
            
            MessagesViewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public boolean showSiteEditDialog(Site site) {
            try {
                    // Load the fxml file and create a new stage for the popup dialog.
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(MainApp.class.getResource("/fxml/SiteEditDialog.fxml"));
                    AnchorPane page = (AnchorPane) loader.load();

                    // Create the dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Редактировать");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);

                    // Set the person into the controller.
                    SiteEditDialogController controller = loader.getController();
                    controller.setDialogStage(dialogStage);
                    controller.setSite(site, this);

                    // Show the dialog and wait until the user closes it
                    dialogStage.showAndWait();

                    return controller.isOkClicked();
            } catch (IOException e) {
                    e.printStackTrace();
                    return false;
            }
    }
    public boolean showSettingsView(){
        try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("/fxml/SettingsView.fxml"));
                AnchorPane page = (AnchorPane) loader.load();    

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Настройки");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);
                
                SettingsViewController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMainApp(this);
                
                dialogStage.showAndWait();
                return controller.isOkClicked();
                
            } catch (IOException e) {
                    e.printStackTrace();
                    return false;
            }
    }
    
public void showStatistics() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/fxml/StatisticsView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Статистика");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        StatisticsViewController controller = loader.getController();
        controller.setData(getPages(),getAdapter());

        dialogStage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    public void loadData() {
      XStream xstream = new XStream();
      xstream.alias("person", Site.class);

      try {
        String xml = FileUtil.readFile(new File("data.xml"));

        ArrayList<SiteWrapper> personList = (ArrayList<SiteWrapper>) xstream.fromXML(xml);

        siteData.clear();
        personList.forEach(f->{
            siteData.add(f.getSite());
        });

        siteData.forEach(file1-> {
            System.out.println(file1.getName()+" "+file1.getChange()+" ");
 //           file1.getPages().forEach(fil->System.out.println(fil));
        });

      } catch (Exception e) { // catches ANY exception
          e.printStackTrace();
      }
    }



    public void saveData() {
      XStream xstream = new XStream();
      xstream.alias("person", Site.class);

      // Convert ObservableList to a normal ArrayList
      ArrayList<SiteWrapper> personList = new ArrayList<>();
      siteData.forEach(f->{
          personList.add(new SiteWrapper(f));
      });
      
      String xml = xstream.toXML(personList);
      try {
        FileUtil.saveFile(xml, new File("data.xml"));


      } catch (Exception e) { // catches ANY exception

      }    
    }
    
    public File getFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
           // primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
         //   primaryStage.setTitle("AddressApp");
        }
    }
    
    public void addFolders(Site site){
        String name = site.getName().replace("http://", "").replaceAll("/", " ");
        try{
            (new File("sites/"+name)).mkdirs();           
            site.getPages().forEach(s->{
                String pageName = s.getName().replace("http://", "").replaceAll("/", " ");
                (new File("sites/"+name+"/"+pageName)).mkdirs();
                    });
           
        } catch(Exception e){
            
        }     
    }
    
    public void addMessage(String name){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        messages.add(new Message(name, time));
    }
    
    public Stage getPrimaryStage() {
            return primaryStage;
    }
    public ObservableList<Site> getSites(){
        return siteData;
    }

    public StatusService getStatusService() {
        return statusService;
    }

    public ChangesService2 getChangesService() {
        return changesService;
    }
    
    public UserPreferences getPreferences(){
        return pref;
    }
    public List<String> getUrl(ObservableList<Site> sites){
        List<String> result = new ArrayList<String>();
        for(Site site:sites){
            result.add(site.getName());
        }
        return result;
    }
    
    public List<String> getPages(){
        List<String> result = new ArrayList<>();
        for(Site site:siteData){
            if(site.getChange()){
                for(Page page:site.getPages()){
                   result.add(page.getName());
               }               
            }

        }
        return result;
    }

    public DBAdapter getAdapter() {
        return adapter;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }
    
    public void createReport(){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Отчет");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 2000);
        sheet.setColumnWidth(4, 2000);
        
        Map<String, Object[]> data = new HashMap<String, Object[]>();
        data.put("1", new Object[] {"Сайт", "За сегодня","За неделю","За все время"});
        
        int i =2;
        
        for(Site site:siteData){
            if(site.getChange()){
                for(Page page: site.getPages()){
                    Integer countToday = adapter.getCountToday(page.getName());
                    Integer countWeek = adapter.getCountWeek(page.getName());
                    Integer countAll = adapter.getCountAll(page.getName());
                    data.put(String.valueOf(i), new Object[]{page.getName(),countToday ,countWeek,countAll});
                    i++;
                }
            }
        }
        
//        data.put("2", new Object[] {"John", 1500000d});
//        data.put("3", new Object[] {"Sam", 800000d});
//        data.put("4", new Object[] {"Dean", 700000d});

        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (int key = 1;key<=keyset.size();key++) {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(String.valueOf(key));
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(obj instanceof Date) 
                    cell.setCellValue((Date)obj);
                else if(obj instanceof Boolean)
                    cell.setCellValue((Boolean)obj);
                else if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Double)
                    cell.setCellValue((Double)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue(new Double(((Integer)obj).doubleValue()));
            }
        }

        try {
            FileOutputStream out = 
                    new FileOutputStream(new File("new.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

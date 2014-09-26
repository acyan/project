/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.controllers;

import com.inspector.util.DBAdapter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author dasha
 */
public class StatisticsViewController {
    @FXML
    private BarChart<Integer, String> chartToday;
    @FXML
    private BarChart<Integer, String> chartWeek;
    @FXML
    private BarChart<Integer, String> chartAll;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private CategoryAxis xAxis1;
    @FXML
    private CategoryAxis xAxis2;
    
    private ObservableList<String> pages = FXCollections.observableArrayList();
    private DBAdapter adapter;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // TODO

    }    
   
    public void setData(List<String> names, DBAdapter adapter){
        
        this.adapter = adapter;
        pages = FXCollections.observableArrayList(names);
        xAxis.setCategories(pages);
        xAxis1.setCategories(pages);
        xAxis2.setCategories(pages);
        
        int[] todayCount = new int[pages.size()];
        int[] weekCount = new int[pages.size()];
        int[] allCount = new int[pages.size()];
        
        for(int i=0;i<pages.size();i++){
            todayCount[i]=  adapter.getCountToday(pages.get(i));
            weekCount[i] = adapter.getCountWeek(pages.get(i));
            allCount[i] = adapter.getCountAll(pages.get(i));            
        }
        
        XYChart.Series<Integer, String> seriesToday = new XYChart.Series<>();
        XYChart.Series<Integer, String> seriesWeek = new XYChart.Series<>();    
        XYChart.Series<Integer, String> seriesAll = new XYChart.Series<>();
        
        for (int i = 0; i < todayCount.length; i++) {
        	seriesToday.getData().add(new XYChart.Data<>(todayCount[i],pages.get(i)));
        }  
        chartToday.getData().add(seriesToday);      
        
        for (int i = 0; i < weekCount.length; i++) {
        	seriesWeek.getData().add(new XYChart.Data<>(weekCount[i],pages.get(i)));
        }   
        chartWeek.getData().add(seriesWeek); 
        
        for (int i = 0; i < allCount.length; i++) {
        	seriesAll.getData().add(new XYChart.Data<>(allCount[i],pages.get(i)));
        }        
        chartAll.getData().add(seriesAll); 
    
    }
    
}

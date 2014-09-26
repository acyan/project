/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.util;

import com.inspector.model.Page;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Даша
 */

public class PageWrapper {
    private String name;
    private String oldSum;
    private String newSum;
    private String title;
    
    public PageWrapper(){
        
    }
    public PageWrapper(String name, String oldSum, String newSum, String title) {
        this.name = name;
        this.oldSum = oldSum;
        this.newSum = newSum;
        this.title = title;
    }
    
    public Page getPage(){
        Page page = new Page(this.name);
        return page;
    }
   
}

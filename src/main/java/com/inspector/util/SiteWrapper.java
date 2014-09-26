/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.util;

import com.inspector.model.Page;
import com.inspector.model.Site;
import com.inspector.util.PageWrapper;
import java.util.ArrayList;
import javafx.collections.FXCollections;

/**
 *
 * @author dasha
 */
public class SiteWrapper {
    private String name;
    private Boolean change;
    private ArrayList<PageWrapper> pages;

    public SiteWrapper(){
        
    }
    public SiteWrapper(Site site) {
        this.name = site.getName();
        this.change = site.getChange();
        
        this.pages = new ArrayList<>();
        site.getPages().forEach(p->this.pages.add(new PageWrapper(p.getName(),p.getOldSum(), p.getNewSum(), p.getTitle())));
    }

    public Site getSite(){
        Site site = new Site(this.name, this.change);
        ArrayList<Page> pages = new ArrayList<>();
        this.pages.forEach(p->pages.add(p.getPage()));
        site.setPages(FXCollections.observableArrayList(pages));
        return site;
    }

    
    
}

package com.inspector.model;

import java.net.Proxy;
import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
 
public class UserPreferences 
{
    private Preferences userPrefs;
    private StringProperty statusFrequency;
    private int changeFrequency;
    private StringProperty proxyAddress;
    private IntegerProperty proxyPort;
    private BooleanProperty proxy;
    
    
    private final static String STATUS = "status";
    private final static String CHANGE = "change";
    private final static String ADDRESS = "address";
    private final static String PORT = "port";
    private final static String PROXY = "proxy";
    
    public UserPreferences()
    {
        userPrefs = Preferences.userRoot().node(this.getClass().getName());
        statusFrequencyProperty().set(userPrefs.get(STATUS, "10000"));
        proxyProperty().set(userPrefs.getBoolean(PROXY, false));
        proxyPortProperty().set(userPrefs.getInt(PORT, 3128));
        proxyAddressProperty().set(userPrefs.get(ADDRESS, "172.16.0.3"));
        
    }

    public Preferences getUserPrefs() {
        return userPrefs;
    }

    public void setUserPrefs(Preferences userPrefs) {
        this.userPrefs = userPrefs;
    }

    public String getProxyAddress() {
        return proxyAddressProperty().get();
    }

    public void setProxyAddress(String proxyAddress) {
        userPrefs.put(ADDRESS, proxyAddress);
        proxyAddressProperty().set(proxyAddress);
    }

    public StringProperty proxyAddressProperty() {
        if (proxyAddress == null) {
            proxyAddress = new SimpleStringProperty();
        }
        return proxyAddress;
    }
    
    public String getStatusFrequency() {
        return statusFrequencyProperty().get();
    }

    public void setStatusFrequency(String statusFrequency) {
        userPrefs.put(STATUS, statusFrequency);
        statusFrequencyProperty().set(statusFrequency);
    }

    public Integer getProxyPort() {
        return proxyPortProperty().get();
    }

    public void setProxyPort(Integer proxyPort) {
        userPrefs.putInt(PORT, proxyPort);
        proxyPortProperty().set(proxyPort);
    }

    public IntegerProperty proxyPortProperty() {
        if (proxyPort == null) {
            proxyPort = new SimpleIntegerProperty();
        }
        return proxyPort;
    }
    
    public StringProperty statusFrequencyProperty() {
        if (statusFrequency == null) {
            statusFrequency = new SimpleStringProperty();
        }
        return statusFrequency;
    }
    
    public Boolean getProxy() {
        return proxyProperty().get();
    }
    public void setProxy(Boolean proxy) {
        userPrefs.putBoolean(PROXY, proxy);
        proxyProperty().set(proxy);
    }

    public BooleanProperty proxyProperty() {
        if (proxy == null) {
            proxy = new SimpleBooleanProperty();
        }
        return proxy;
    }
}
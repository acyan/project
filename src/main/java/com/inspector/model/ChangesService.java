/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import com.sun.corba.se.spi.copyobject.CopierManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author dasha
 */
public class ChangesService extends ScheduledService<BlockingQueue>{

    private BlockingQueue<String> sites;
    
    
    public BlockingQueue<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites =null;
        this.sites = new LinkedBlockingQueue<>(sites);
    }
    
    public ChangesService(List<String> sites) {
        this.sites = new LinkedBlockingQueue<>(sites);
    }
    
    @Override
    protected Task<BlockingQueue> createTask() {
        final Task<BlockingQueue> task;
        
        task = new Task<BlockingQueue>() {
            BlockingQueue<String> result = new LinkedBlockingQueue<>();
            Element doc;
            String text;
            String md5;
            @Override
            protected BlockingQueue<String> call(){
                try{
                    for(String page:sites){
                        URL site = new URL(page);
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.16.0.3", 3128));
                        HttpURLConnection connection = (HttpURLConnection) site.openConnection();
                        
                        connection.setRequestMethod("GET");

                        connection.setReadTimeout(10000);
                        connection.connect();
                        int code = connection.getResponseCode();
                        
                        if(code == 302){
                            site = new URL(connection.getHeaderField("Location"));
                            connection = (HttpURLConnection) site.openConnection();
                        }
                        
                        String line = null;
                        StringBuffer tmp = new StringBuffer();
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = in.readLine()) != null) {
                          tmp.append(line);
                        }
                        in.close();
                        connection.disconnect();
                        doc = Jsoup.parse(String.valueOf(tmp)).select("body").first();
                        text = doc.text();
                        md5 = md5Custom(text);
                        result.add(md5);
                    }                    
                } catch(Exception e){
                    e.printStackTrace();
                } catch(Error e){
                    e.printStackTrace();
                }

                
                System.out.println("\nFinished changes "+getPeriod());                  
                return result;
                
            }
        };
        return task;        
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.Response;
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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author dasha
 */
public class ChangesService2 extends ScheduledService<CopyOnWriteArrayList<Site>>{

    private CopyOnWriteArrayList<Site> sites;
    private AsyncCompletionHandler<Integer> asyncCompletionHandler;
    private UserPreferences userPreferences;
    
    public CopyOnWriteArrayList<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites =null;
        this.sites = new CopyOnWriteArrayList<>(sites);
    }
    
    public ChangesService2(List<Site> sites, UserPreferences userPreferences) {
        this.sites = new CopyOnWriteArrayList<>(sites);
        this.userPreferences = userPreferences;
        this.asyncCompletionHandler = new AsyncCompletionHandler<Integer>() {
            int status;

            @Override
            public AsyncHandler.STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                this.status = status.getStatusCode();
                return AsyncHandler.STATE.ABORT;
            }

            @Override
            public Integer onCompleted(Response response) throws Exception {
                return status;
            }
        };
    }
    
    @Override
    protected Task<CopyOnWriteArrayList<Site>> createTask() {
        final Task<CopyOnWriteArrayList<Site>> task;
        
        task = new Task<CopyOnWriteArrayList<Site>>() {
            CopyOnWriteArrayList<Site> result = new CopyOnWriteArrayList<Site>();
            Element doc;
            String text;
            String md5;
            @Override
            protected CopyOnWriteArrayList<Site> call(){
                System.out.println(System.currentTimeMillis());
                AsyncHttpClient asyncHttpClient = null;
                AsyncHttpClientConfig.Builder config = new AsyncHttpClientConfig.Builder();
                config.setConnectionTimeoutInMs(1000).setFollowRedirects(true);
                config.setIdleConnectionInPoolTimeoutInMs(1000);
                config.setMaximumConnectionsPerHost(10);
                config.setMaximumConnectionsTotal(100);
                ProxyServer proxyServer = null;
                
                try{
                    asyncHttpClient = new AsyncHttpClient(config.build());
                    for(Site site:sites){
                        try{
                            if(site.getChange()){
                                for(Page page: site.getPages()){
                                    Future<Response> r = asyncHttpClient.prepareGet(page.getName()).execute();
                                    Response response = r.get();
                                    String string = response.getResponseBody();
                                    Document document = Jsoup.parse(String.valueOf(string));
                                    String title = document.title();
                                    doc = document.select("body").first();
                                    text = doc.text();
                                    md5 = md5Custom(text);
                                    page.setOldSum(page.getNewSum());                                
                                    page.setNewSum(md5);
//                                    if(code == 302){
//                                        siteURL = new URL(connection.getHeaderField("Location"));
//                                        connection = (HttpURLConnection) siteURL.openConnection(proxy);
//                                    } else if (code == 200){
//                                        String line = null;
//                                        StringBuffer tmp = new StringBuffer();
//                                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                                        while ((line = in.readLine()) != null) {
//                                          tmp.append(line);
//                                        }
//                                        in.close();
//                                        String encoding = connection.getContentEncoding();                                
//                                        connection.disconnect();
//                                        Document document = Jsoup.parse(String.valueOf(tmp));
//                                        String title = document.title();
//
//                                        page.setTitle(title);
//
//                                        doc = document.select("body").first();
//                                        text = doc.text();
//                                        md5 = md5Custom(text);
//                                        page.setOldSum(page.getNewSum());                                
//                                        page.setNewSum(md5);                                    
//                                    }

                                }
                                

                            }
                        } catch(Exception e){
                            e.printStackTrace();
                            result.add(site);
                        }
                        result.add(site);  
                    }                    
                } catch(Exception e){
                    e.printStackTrace();
                } finally{
                    asyncHttpClient.close();
                }

                 System.out.println(System.currentTimeMillis());               
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import com.inspector.util.Status;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.Response;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
/**
 *
 * @author dasha
 */
public class StatusService extends ScheduledService<BlockingQueue>{

    private CopyOnWriteArrayList<String> sites;
    private AsyncCompletionHandler<Integer> asyncCompletionHandler;
    UserPreferences userPreferences;
    
    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = new CopyOnWriteArrayList(sites);

    }
    
    public StatusService(List<String> sites, UserPreferences userPreferences) {
        this.sites = new CopyOnWriteArrayList(sites);
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
    protected Task<BlockingQueue> createTask() {
        final Task<BlockingQueue> task;
        task = new Task<BlockingQueue>(){
        
            
            @Override
            protected BlockingQueue call() throws Exception { 
                BlockingQueue result = new LinkedBlockingQueue<String>();
                AsyncHttpClient asyncHttpClient = null;
                AsyncHttpClientConfig.Builder config = new AsyncHttpClientConfig.Builder();
                config.setConnectionTimeoutInMs(1000).setFollowRedirects(true);
                config.setIdleConnectionInPoolTimeoutInMs(15000);
                config.setMaximumConnectionsPerHost(10);
                config.setMaximumConnectionsTotal(100);
                ProxyServer proxyServer = null;
                try{
                    asyncHttpClient = new AsyncHttpClient(config.build());
                   
                    if(userPreferences.getProxy())
                        proxyServer = new ProxyServer(userPreferences.getProxyAddress(),userPreferences.getProxyPort());
                    List<Future<Integer>> results = new ArrayList<Future<Integer>>();
                    for(String site:sites){
                        Future<Integer> future = asyncHttpClient.prepareGet(site).setFollowRedirects(true).setProxyServer(proxyServer).execute(asyncCompletionHandler);
                        results.add(future);
                    }
                    
                    for(Future<Integer> element:results){
                        try{
                            int  i = element.get();
                            if(element.get()==200){
                                result.put(Status.ACTIVE.getValue());
                            } else {
                                result.put(Status.INACTIVE.getValue());
                            }
                        } catch(Exception e) {
                            result.put(Status.INACTIVE.getValue());
                        }
                    }
                    
                } catch(Exception e){
                    e.printStackTrace();
                } finally{
                    asyncHttpClient.close();
                }
            System.out.println("\nFinished all threads "+getPeriod());    
                return result;
            }
        };
        return task;
    }
}

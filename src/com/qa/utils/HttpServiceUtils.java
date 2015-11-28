package com.qa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;


public class HttpServiceUtils
{
    private static Logger logger = Logger.getLogger(HttpServiceUtils.class);
    private static final Map<Integer, CloseableHttpClient> clientsMap = new ConcurrentHashMap();
    
    private static CloseableHttpClient getNewHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    private static CloseableHttpClient getHttpClient(HttpRequestBase request) {
        Integer id = getClientId(request);
        CloseableHttpClient closeableHttpClient = clientsMap.get(id);
        if (closeableHttpClient == null) {
            closeableHttpClient = getNewHttpClient();
            clientsMap.put(id, closeableHttpClient);
        }
        return closeableHttpClient;
    }
    
    private static Integer getClientId(HttpRequestBase request) {
        return request.getURI().getHost().hashCode() + Thread.currentThread().hashCode();
    }
    
    private static String getContent(HttpResponse response){
        if (response.getStatusLine().getStatusCode() > 500)
            AssertHelper.fail("got: " + getContentNoCheck(response));
        return getContentNoCheck(response);
    }
    
    private static String getContentNoCheck(HttpResponse response) {
        InputStream in = null;
        String result = "";
        try {
            in = response.getEntity().getContent();
            StringBuilder builder = new StringBuilder();
            byte[] buffer = new byte[100];
            int readResult = in.read(buffer);
            while(readResult >= 0) {
                builder.append(new String(buffer, 0, readResult));
                readResult = in.read(buffer);
            }
            String res = builder.toString();
            logger.info("RESPONSE: " + res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }        
        return result;
    }
    
    public static String doGet(String url){
        return doRequest(new HttpGet(url));
    }
    
    public static String doPost(String url, String body){
        return doRequest(new HttpPost(url), body);
    }
    
    private static String doRequest(HttpRequestBase request){
        return doRequest(request, null);
    }
    
    private static String doRequest(HttpRequestBase request, String body){
        if (request.getURI() == null || request.getURI().toString().isEmpty())
            AssertHelper.fail("Request URL is not specified");

        logger.info(String.format("Request: %s %s", request.getMethod(), request.getURI()));
        if(body != null){
            StringEntity json = new StringEntity(body, "UTF-8");
            json.setContentType("application/json");
            ((HttpPost)request).setEntity(json);
            logger.info("Body: " + body);
        }
        
        try{
            return getContent(getHttpClient(request).execute(request));
        }catch(ClientProtocolException e){
            AssertHelper.fail("Protocol exception occurred on executing GET request:\n" + e.getMessage());
        }catch(IOException e){
            AssertHelper.fail("Unexpected error occurred on executing GET request:\n" + e.getMessage());
        }
        return null;
    }
}


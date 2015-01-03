package com.example.testapp.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
 
public class JSONParser extends Activity{
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    
    /**The objects of this class contain the query*/
    private static class TaskParams {
        String url;
        List<NameValuePair> params;

        TaskParams(String a,List<NameValuePair> b) {
            this.url = a;
            this.params = b;
        }
    }

    // constructor
    public JSONParser() {
 
    }
 
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {
    	/**Parse the input request from various intents and send the query to the server*/
    	TaskParams parameter = new TaskParams(url,params); 				
    	BetterWay betterway = new BetterWay();
    	try
    	{
    		jObj=(JSONObject) betterway.execute(parameter).get(50L,TimeUnit.SECONDS);
    	}
    	 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return jObj;
       
    }
    
    /**Class to handle the query.The processing is done asynchronously*/
    class BetterWay extends AsyncTask<TaskParams, Void, JSONObject> {
    	
    	protected void onPreExecute(JSONObject result) {
    		
        }

        protected JSONObject doInBackground(TaskParams... input) {
        	String url=input[0].url;
        	List<NameValuePair> params = input[0].params;
        	 // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
     
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                System.out.println("Request successful");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
     
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "n");
                }
                is.close();
                json = sb.toString();
                System.out.println(json);
            } catch (Exception e) {
            	System.out.println("No results");
            }
     
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);            
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            
            // return JSON String
            return jObj;
     
            
        }

        /*nothing to be done on post execute as the object is returned in the previous function*/
        protected void onPostExecute(JSONObject result) {
        }
    }
}

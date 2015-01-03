package com.example.testapp.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String URL = "http://172.27.22.35/project/";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String add_tag = "add";
    private static String delete_tag = "delete";
    private static String edit_tag = "edit";
     
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
     
    
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        return json;
    }
     
    public JSONObject addUser(String uid,String contact_name, String contact_org,
    		String contact_descr, String contact_place, String contact_image) {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", add_tag));
        params.add(new BasicNameValuePair("uid",uid));
        params.add(new BasicNameValuePair("name", contact_name));
        params.add(new BasicNameValuePair("organisation",contact_org));
        params.add(new BasicNameValuePair("description", contact_descr));
        params.add(new BasicNameValuePair("place", contact_place));
        params.add(new BasicNameValuePair("image", contact_image));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        return json;
	}
    
    public JSONObject editUser(String uid,String contact_id,String contact_name, String contact_org,
    		String contact_descr, String contact_place, String contact_image) {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", edit_tag));
        params.add(new BasicNameValuePair("uid",uid));
        params.add(new BasicNameValuePair("contact_id",contact_id));
        params.add(new BasicNameValuePair("name", contact_name));
        params.add(new BasicNameValuePair("organisation",contact_org));
        params.add(new BasicNameValuePair("description", contact_descr));
        params.add(new BasicNameValuePair("place", contact_place));
        params.add(new BasicNameValuePair("image", contact_image));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        return json;
	}
    
    public JSONObject deleteUser(String uid,String contact_id)
    {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", delete_tag));
        params.add(new BasicNameValuePair("uid",uid));
        params.add(new BasicNameValuePair("contact_id",contact_id));
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        return json;
    }
    	
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }


	
     
}

/**Display list of favourites*/ 
package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.testapp.library.UserFunctions;

public class Favourites extends Activity {
	
	private Spinner spinner;
	
	/**Results are displayed in a listview.The header contains the name and the picture.The child contains all the information associated with the header*/
	List<String> listDataHeader;
	HashMap<String,JSONObject> listDataChild;
	ProgressBar bar;
	ExpandableListView lview;
	
	TextView text1;
	private static String uid;
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	private static String URL = "http://172.27.22.35/project/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourites);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
			uid = extras.getString("user_name");
		
		bar = (ProgressBar) findViewById(R.id.progress);
		text1 = (TextView)findViewById(R.id.errortext);
		spinner = (Spinner) findViewById(R.id.spinner1);
		lview = (ExpandableListView) findViewById(R.id.listview);
		
		/**Options for displaying the desired number of contacts*/
		List<String> list = new ArrayList<String>();
		list.add("5");
		list.add("10");
		list.add("15");
		list.add("20");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			/**Send request to the server to fetch the details of "count" number of users*/
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String count =(String) parent.getItemAtPosition(pos);
				System.out.println(count);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("tag","recent"));
				params.add(new BasicNameValuePair("uid",uid));
				params.add(new BasicNameValuePair("count",count));
				TaskParams parameter = new TaskParams(URL,params);
				BetterWay betterway = new BetterWay();
				betterway.execute(parameter);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favourites, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_logout:
			Intent i = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**The context menu displays two options viz. edit the contact or delete the contact*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		System.out.println("Context menu");
		super.onCreateContextMenu(menu, v, menuInfo);
		ExpandableListView.ExpandableListContextMenuInfo info =(ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		menu.setHeaderTitle(listDataHeader.get(group));
		String arr[] = {"Edit contact","Delete contact"};
		for(int i=0;i<arr.length;i++)
			menu.add(0,i,i,arr[i]);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) 
	
	{
		ExpandableListContextMenuInfo info =(ExpandableListContextMenuInfo) menuItem.getMenuInfo();
		int groupPos = 0;
		groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		System.out.println(groupPos+" "+listDataHeader.size());
		JSONObject obj = listDataChild.get(listDataHeader.get(groupPos));
		System.out.println(obj+""+menuItem.getItemId());
		switch(menuItem.getItemId())
		{
			case 0: //edit the contact
				Intent i = new Intent(getApplicationContext(),Edit.class);
				i.putExtra("user_name",uid);
			try {
				i.putExtra("contact_id",obj.getString("contact_id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				i.putExtra("name",obj.getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				i.putExtra("place",obj.getString("place"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				i.putExtra("organisation",obj.getString("organisation"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				i.putExtra("description",obj.getString("description"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				i.putExtra("image",obj.getString("image"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				startActivity(i);
				break;
			
			case 1://delete the contact
				UserFunctions f = new UserFunctions();
			try {
				f.deleteUser(uid,obj.getString("contact_id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				finish();
				break;
		}
		return super.onContextItemSelected(menuItem);
	}
	
	/*Asynchronous processing of the task.Various UI elements are taken care of here*/
	private static class TaskParams {
		String url;
		List<NameValuePair> params;

		TaskParams(String a,List<NameValuePair> b) {
			this.url = a;
			this.params = b;
		}
	}

	class BetterWay extends AsyncTask<TaskParams, Void, JSONObject> {

		protected void onPreExecute() {

			bar.setVisibility(View.VISIBLE);
			lview.setVisibility(View.GONE);
			text1.setVisibility(View.GONE);

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
			
			return jObj;

		}

		protected void onPostExecute(JSONObject result) {

			//hide the spinner and populate the output
			bar.setVisibility(View.GONE);
			if(result==null)
			{
				text1.setText("Unknown error.Please try again");
				text1.setVisibility(View.VISIBLE);
			}
			else
			{
				try {
					int count = Integer.parseInt(result.getString("count"));
					if(count==0)
					{
						System.out.println("no results");
						text1.setText("No favourites found!");
						text1.setVisibility(View.VISIBLE);
					}
					else
					{
						listDataHeader = new ArrayList<String>();
						listDataChild = new HashMap<String,JSONObject>();
						for(int i=0;i<count;i++)
						{
							JSONObject obj = result.getJSONObject(Integer.toString(i));
							listDataHeader.add(obj.getString("name"));
							listDataChild.put(obj.getString("name"),obj);
						}
						ExpandableListAdapter listAdapter = new ExpandableListAdapter(Favourites.this, listDataHeader, listDataChild);
						lview.setAdapter(listAdapter);
						registerForContextMenu(lview);
						lview.setVisibility(View.VISIBLE);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

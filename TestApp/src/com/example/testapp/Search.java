/*Handles the search intent and displays the search results*/
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

import com.example.testapp.library.UserFunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Search extends Activity implements View.OnClickListener{

	/**Results are displayed in a listview.The header contains the name and the picture.The child contains all the information associated with the header*/
	List<String> listDataHeader;
	HashMap<String,JSONObject> listDataChild;
	private static final int VOICE_REQUEST = 1001;
	Button search;
	ProgressBar bar;
	ExpandableListView lview;
	
	EditText t1,t2,t3;
	ImageView add1,add2,sub2,sub3,v1,v2,v3;
	TextView text1;
	
	private static String uid;
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	private static String URL = "http://172.27.22.35/project/";
	private static int vid=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
			uid = extras.getString("user_name");


		search = (Button) findViewById(R.id.search);
		bar = (ProgressBar) findViewById(R.id.progress);
		lview = (ExpandableListView) findViewById(R.id.listview);
		text1 = (TextView) findViewById(R.id.errortext);

		/**Query input fields*/
		t1 = (EditText) findViewById(R.id.searchText1);
		t2 = (EditText) findViewById(R.id.searchText2);
		t3 = (EditText) findViewById(R.id.searchText3);

		/**Add/remove a query input field*/
		add1 = (ImageView) findViewById(R.id.searchViewAdd1);
		add2 = (ImageView) findViewById(R.id.searchViewAdd2);
		sub2 = (ImageView) findViewById(R.id.searchViewSub2);
		sub3 = (ImageView) findViewById(R.id.searchViewSub3);

		/**Handle voice input*/
		v1 = (ImageView) findViewById(R.id.voice_q1);
		v2 = (ImageView) findViewById(R.id.voice_q2);
		v3 = (ImageView) findViewById(R.id.voice_q3);
		
		v1.setOnClickListener(this);
		v2.setOnClickListener(this);
		v3.setOnClickListener(this);
		
		add1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				t2.setVisibility(View.VISIBLE);
				add2.setVisibility(View.VISIBLE);
				sub2.setVisibility(View.VISIBLE);
				v2.setVisibility(View.VISIBLE);
			}
		});

		sub2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				t2.setVisibility(View.GONE);
				t2.setText("");
				add2.setVisibility(View.GONE);
				sub2.setVisibility(View.GONE);
				v2.setVisibility(View.GONE);
			}
		});

		add2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				t3.setVisibility(View.VISIBLE);
				sub3.setVisibility(View.VISIBLE);
				v3.setVisibility(View.VISIBLE);
			}
		});

		sub3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				t3.setVisibility(View.GONE);
				t3.setText("");
				sub3.setVisibility(View.GONE);
				v3.setVisibility(View.GONE);
			}
		});

		/**Send search request to the server*/ 
		search.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				String s1=null,s2=null,s3=null;
				if(t1.getText()!=null)
					s1 = t1.getText().toString();
				if(t2.getText()!=null)
					s2 = t2.getText().toString();
				if(t3.getText()!=null)
					s3 = t3.getText().toString();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("tag","search"));
				params.add(new BasicNameValuePair("uid",uid));
				params.add(new BasicNameValuePair("string1",s1));
				params.add(new BasicNameValuePair("string2",s2));
				params.add(new BasicNameValuePair("string3",s3));
				TaskParams parameter = new TaskParams(URL,params);
				BetterWay betterway = new BetterWay();
				betterway.execute(parameter);
			}

		});

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
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

	private static class TaskParams {
		String url;
		List<NameValuePair> params;

		TaskParams(String a,List<NameValuePair> b) {
			this.url = a;
			this.params = b;
		}
	}

	/**Aysnchronously handle the search request and populate the output list*/
	class BetterWay extends AsyncTask<TaskParams, Void, JSONObject> {

		protected void onPreExecute() {

			/**Display the spinner*/
			search.setVisibility(View.GONE);
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

			/**Result obtained.Remove the spinner*/
			bar.setVisibility(View.GONE);
			search.setVisibility(View.VISIBLE);
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
						text1.setText("No results found!");
						text1.setVisibility(View.VISIBLE);
					}
					else
					{
						/**Populate the list view*/
						listDataHeader = new ArrayList<String>();
						listDataChild = new HashMap<String,JSONObject>();
						for(int i=0;i<count;i++)
						{
							JSONObject obj = result.getJSONObject(Integer.toString(i));
							listDataHeader.add(obj.getString("name"));
							listDataChild.put(obj.getString("name"),obj);
						}
						ExpandableListAdapter listAdapter = new ExpandableListAdapter(Search.this, listDataHeader, listDataChild);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		/**The context menu has options for editing a contact and deleting a contact*/
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
	public boolean onContextItemSelected(MenuItem menuItem) {
		ExpandableListContextMenuInfo info =(ExpandableListContextMenuInfo) menuItem.getMenuInfo();
		int groupPos = 0;
		groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		System.out.println(groupPos+" "+listDataHeader.size());
		JSONObject obj = listDataChild.get(listDataHeader.get(groupPos));
		System.out.println(obj+""+menuItem.getItemId());
		switch(menuItem.getItemId())
		{
			case 0: //edit contact
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
			
			case 1: //delete contact
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
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		vid = v.getId();
		System.out.println("Voice started");
		startActivityForResult(i, VOICE_REQUEST);
	}
	
	/**Handle camera and voice post processing*/
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	if(resultCode==RESULT_OK)
	    	{
	    		if(requestCode==VOICE_REQUEST)
	    		{
	    			ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	    			System.out.println(vid+" "+textMatchList.size());
	    			EditText txt;
	    			if(textMatchList.size()!=0)
	    			{
	    				switch(vid)
	    				{
	    					case R.id.voice_q1:
	    						txt = (EditText)findViewById(R.id.searchText1);
	    						txt.setText(textMatchList.get(0));
	    						break;
	    					case R.id.voice_q2:
	    						txt = (EditText)findViewById(R.id.searchText2);
	    						txt.setText(textMatchList.get(0));
	    						break;
	    					case R.id.voice_q3:
	    						txt = (EditText)findViewById(R.id.searchText3);
	    						txt.setText(textMatchList.get(0));
	    						break;
	    				}
	    			}
	    		}
	    	}
	 }
}

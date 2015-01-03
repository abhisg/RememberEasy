/*The main screen for the user.Spawned by Register and add activities.Creates intent for adding user,searching user and viewing the list of favourites*/
package com.example.testapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Dashboard extends Activity {

	TextView nam,active;
	static String nam_str,active_str,uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(true);
	    
	    Bundle extras = getIntent().getExtras();
	    /**Obtain the information related to the user who has logged in*/
	    if(extras!=null)
	    {
	    	nam_str = "Welcome "+extras.getString("name");
	    	active_str = "You last logged in at "+extras.getString("last");
	    	uid = extras.getString("uid");
	    }
	   
	    nam = (TextView) findViewById(R.id.welcomename);
	    nam.setText(nam_str);
	    active = (TextView) findViewById(R.id.lastlogin);
	    active.setText(active_str);
	    
	    Button search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new View.OnClickListener() {
			 
            public void onClick(View v) {
                // Switching to search screen
                Intent i = new Intent(getApplicationContext(), Search.class);
                startActivity(i);
                i.putExtra("user_name",uid);
                startActivity(i);
            }
        });
		
		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new View.OnClickListener() {
			 
            public void onClick(View v) {
                // Switching to add screen
                Intent i = new Intent(getApplicationContext(), Add.class);
                i.putExtra("user_name",uid);
                startActivity(i);
            }
        });
		
		Button fav = (Button) findViewById(R.id.fav);
		fav.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Switching to favourites screen
				Intent i = new Intent(getApplicationContext(),Favourites.class);
				i.putExtra("user_name",uid);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**Handle the action bar*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	        	Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
	            return true;
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
		
}


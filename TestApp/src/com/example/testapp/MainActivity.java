/*The login screen.If correct credentials are provided,brings the user home screen*/
package com.example.testapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testapp.library.DatabaseHandler;
import com.example.testapp.library.UserFunctions;

public class MainActivity extends Activity {

	Button login;
	TextView registerScreen;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;

	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});
		
		/**Handle login*/
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		login = (Button) findViewById(R.id.btnLogin);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		login.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//switch to the home screen of the user
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.loginUser(email, password);

				try {
					if (json!=null && json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");

							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        

							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), Dashboard.class);
							dashboard.putExtra("name",json_user.getString(KEY_NAME));
                            dashboard.putExtra("last",json_user.getString(KEY_CREATED_AT));
                            dashboard.putExtra("uid", json.getString(KEY_UID));
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);

							// Close Login Screen
							finish();
						}else{
							// Error in login
							loginErrorMsg.setText("Incorrect username/password");
							loginErrorMsg.setVisibility(View.VISIBLE);
						}
					}
					else
					{
						loginErrorMsg.setText("Unknown error.Please try again");
						loginErrorMsg.setVisibility(View.VISIBLE);
		
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

/*The main screen for the user.Spawned by Register and add activities.Creates intent for adding user,searching user and viewing the list of favourites*/
package com.example.testapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testapp.library.UserFunctions;

public class Edit extends Activity implements View.OnClickListener{

	private static final int REQUEST_CODE = 1;
	private static String uid;
	
	 /**UI elements associated with the input*/
    private Bitmap bitmap;
    private ImageView clickpic;
    private ImageView uploadpic;
    private ImageView voice_name;
    private ImageView voice_place;
    private ImageView voice_organisation;
    private ImageView voice_description;
    private ImageView pic;
    private EditText name;
    private EditText place;
    private EditText description;
    private EditText org;
    String contact_id;
    String contact_name;
    String contact_org;
    String contact_descr;
    String contact_place;
    String contact_image;
    
    private static final int CAMERA_REQUEST = 1888;
    private static final int VOICE_REQUEST = 1001;
    private static int id=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_add);
 
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        
        /**Load the input with the already existing values*/
        if(extras!=null)
        {
        	uid = extras.getString("user_name");
        	contact_id = extras.getString("contact_id");
        	contact_name = extras.getString("name");
            contact_org = extras.getString("organisation");
            contact_descr = extras.getString("description");
            contact_place = extras.getString("place");
            contact_image = extras.getString("image");
        }
        
        pic = (ImageView) findViewById(R.id.icon);
        name = (EditText) findViewById(R.id.fullname);
        org = (EditText) findViewById(R.id.organisation);
        description = (EditText) findViewById(R.id.describe);
        place = (EditText) findViewById(R.id.place);
        
        byte[] decodedByte = null;
		decodedByte = Base64.decode(contact_image, 0);
		if(decodedByte.length!=0)
		{
			pic.setVisibility(View.VISIBLE);
			pic.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
		}
		
		name.setText(contact_name);
		org.setText(contact_org);
		description.setText(contact_descr);
		place.setText(contact_place);
		
        clickpic = (ImageView)findViewById(R.id.clickpic);
        clickpic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
					cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.tmp")));
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});
        
        uploadpic = (ImageView)findViewById(R.id.uploadpic);
        uploadpic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        intent.addCategory(Intent.CATEGORY_OPENABLE);
		        startActivityForResult(intent, REQUEST_CODE);
			}
		});
        
        voice_name = (ImageView)findViewById(R.id.voice_name);
        voice_place = (ImageView)findViewById(R.id.voice_place);
        voice_organisation = (ImageView)findViewById(R.id.voice_organisation);
        voice_description = (ImageView)findViewById(R.id.voice_describe);
        
        voice_name.setOnClickListener(this);
        voice_place.setOnClickListener(this);
        voice_organisation.setOnClickListener(this);
        voice_description.setOnClickListener(this);
        
        /*Modify the input and send the edit request to the server*/
        TextView loginScreen = (TextView) findViewById(R.id.done);
        loginScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View arg0) {
                        
            	ProgressBar bar = (ProgressBar) findViewById(R.id.progress2);
            	bar.setVisibility(View.VISIBLE);
            	
            	contact_name = name.getText().toString();
                contact_org = org.getText().toString();
                contact_descr = description.getText().toString();
                contact_place = place.getText().toString();
                
                if(bitmap!=null)
                {
                	ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                	bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                	byte [] b=baos.toByteArray();
                	contact_image = Base64.encodeToString(b, Base64.DEFAULT);
                }
                UserFunctions userFunction = new UserFunctions();
                
                JSONObject json = userFunction.editUser(uid,contact_id,contact_name,contact_org,contact_descr,contact_place,contact_image);
                TextView txt = (TextView) findViewById(R.id.add_error);
                if(json!=null)
                {
                	bar.setVisibility(View.GONE);
                	txt.setText("Contact edited!");
                	txt.setVisibility(View.VISIBLE);
                    finish();
                }
                else
                {
                	System.out.println("Edit failed");
                	bar.setVisibility(View.GONE);
                	txt.setText("Failed to add contact.Try again");
                	txt.setVisibility(View.VISIBLE);
                }
            }
        });
        
        Button reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText txt = (EditText) findViewById(R.id.fullname);
				txt.setText("");
				txt = (EditText) findViewById(R.id.place);
				txt.setText("");
				txt = (EditText) findViewById(R.id.organisation);
				txt.setText("");
				txt = (EditText) findViewById(R.id.describe);
				txt.setText("");
				pic.setVisibility(View.GONE);
			}
		});
     }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode==RESULT_OK)
    	{
    		if(requestCode==CAMERA_REQUEST)
    		{
    				if(bitmap!=null)
    					bitmap.recycle();
    				InputStream stream = null;
    				File file = new File(Environment.getExternalStorageDirectory(),"image.tmp");
    				try {
    			        stream=new FileInputStream(file);
    			    } catch (FileNotFoundException e) {
    			        e.printStackTrace();
    			    }
    				if(stream==null){
    			        try {
    			            Uri u = data.getData();
    			            stream=getContentResolver().openInputStream(u);
    			        } catch (FileNotFoundException e) {
    			            e.printStackTrace();
    			        }
    			    }
    				BitmapFactory.Options o2 = new BitmapFactory.Options();
    		        o2.inSampleSize = 16;
    				bitmap = BitmapFactory.decodeStream(stream,null,o2);
    				try{
    					stream.close();
    					pic.setVisibility(View.VISIBLE);
    					pic.setImageBitmap(bitmap);
    					file.delete();
    				} catch (FileNotFoundException e) {
                    e.printStackTrace();
    				} catch (IOException e) {
                    e.printStackTrace();
                }				
    		}
    		else if(requestCode==VOICE_REQUEST)
    		{
    			ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    			System.out.println(id+" "+textMatchList.size());
    			EditText txt;
    			if(textMatchList.size()!=0)
    			{
    				switch(id)
    				{
    					case R.id.voice_name:
    						txt = (EditText)findViewById(R.id.fullname);
    						txt.setText(textMatchList.get(0));
    						break;
    					case R.id.voice_describe:
    						txt = (EditText)findViewById(R.id.description);
    						txt.setText(textMatchList.get(0));
    						break;
    					case R.id.voice_organisation:
    						txt = (EditText)findViewById(R.id.organisation);
    						txt.setText(textMatchList.get(0));
    						break;
    					case R.id.voice_place:
    						txt = (EditText)findViewById(R.id.place);
    						txt.setText(textMatchList.get(0));
    						break;
    				}
    			}
    			else
    				System.out.println("Nothing in the input");

    		}
    	    else /*Obtain the picture selected from the gallery of the phone*/
    		{
    			try {
    				if (bitmap != null) 
                        bitmap.recycle();
    				
    				Uri selectedImage = data.getData();
    		        BitmapFactory.Options o2 = new BitmapFactory.Options();
    		        o2.inSampleSize = 16;
    		        
                    InputStream stream = getContentResolver().openInputStream(selectedImage);
                    bitmap = BitmapFactory.decodeStream(stream,null,o2);
                    stream.close();
                    
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
    		}
    		super.onActivityResult(requestCode, resultCode, data);
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return super.onCreateOptionsMenu(menu);
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

	@Override
	public void onClick(View v) {
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		id = v.getId();
		System.out.println("Voice started");
		startActivityForResult(i, VOICE_REQUEST);
	}
}

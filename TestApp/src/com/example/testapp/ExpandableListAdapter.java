/*Handles the list displayed during search and favourites.Populates the empty list view*/
package com.example.testapp;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private HashMap<String, JSONObject> _listDataChild;
    
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,HashMap<String, JSONObject> listDataChild) 
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
    }
 
    /**The listview parent has a single child which displays the details of the user*/
    @Override
    public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) 
    {
 
    	/**The child object contains the user details*/
        final JSONObject child = (JSONObject) getChild(groupPosition, childPosition);
 
        if (convertView == null) 
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView1);
        byte[] decodedByte = null;
		try {
			decodedByte = Base64.decode(child.getString("image"), 0);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(decodedByte.length!=0)
			img.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
        
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        try {
			name.setText(child.getString("name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        TextView place = (TextView) convertView.findViewById(R.id.metAt);
        try {
			place.setText("You met this person at "+child.getString("place"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        TextView organisation = (TextView) convertView.findViewById(R.id.worksAt);
        try {
			organisation.setText("This person works at "+child.getString("organisation"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        TextView description = (TextView) convertView.findViewById(R.id.description);
        try {
			description.setText(child.getString("description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    /**This is the listview parent.It contains the name and photo of the user in the list tab*/
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        
    	String headerTitle = (String) getGroup(groupPosition);
        final JSONObject child = (JSONObject) getChild(groupPosition,0);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        
        ImageView lblListImage = (ImageView) convertView.findViewById(R.id.lblListImage);
        byte[] decodedByte = null;
		try {
			decodedByte = Base64.decode(child.getString("image"), 0);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(decodedByte.length!=0)
			lblListImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
}

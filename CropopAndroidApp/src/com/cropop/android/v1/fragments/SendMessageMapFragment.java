package com.cropop.android.v1.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cropop.android.v1.R;
import com.google.android.gms.maps.SupportMapFragment;

public class SendMessageMapFragment extends SupportMapFragment {
	
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.send_message_map, container, false);
//    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
	    // Inflate the menu items for use in the action bar
//		MenuInflater inflater = getActivity().getMenuInflater();
//		menu.setTitle("Locate the target...");
//	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//	    getSupportActionBar().setsetHasOptionsMenu(true);
		
//		menuInflater.inflate(R.menu.map_activity_actions, menu);
	    super.onCreateOptionsMenu(menu, menuInflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
         case R.id.action_done:
//         	getActivity()
     }
     return false;
	}
	
}

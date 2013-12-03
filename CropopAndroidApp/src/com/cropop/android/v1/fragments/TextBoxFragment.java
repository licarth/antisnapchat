package com.cropop.android.v1.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class TextBoxFragment extends Fragment {
	
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.send_message_map, container, false);
//    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		setHasOptionsMenu(true);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}

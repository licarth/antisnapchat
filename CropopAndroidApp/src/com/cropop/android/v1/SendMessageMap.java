package com.cropop.android.v1;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.animation.BounceInterpolator;

import com.cropop.android.v1.manager.MyLocationManager;
import com.cropop.android.v1.service.LocationWatchService;
import com.cropop.android.v1.util.ColorUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SendMessageMap extends ActionBarActivity {
	private GoogleMap mMap;
	Marker marker;
	static final LatLng CENTRALE_PARIS = new LatLng(48.7638176721, 2.2886595502);
	final Intent intent = new Intent();
	private LocationManager locMgr = null;
	private Criteria crit=new Criteria();
	private Circle circle;
	private int circleColor = ColorUtils.setTransparency(Color.parseColor("#ABFACF"), 45);
	private int circleStrokeColor = ColorUtils.setTransparency(Color.parseColor("#39942A"), 100);
	private float circleStrokeWidth = 2.0F;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message_map);
		setTitle("Drop your pop...");
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//	    getSupportActionBar().setHasOptionsMenu(true);
		
//	    startSupportActionMode(new ActionMode.Callback() {
//	        @Override
//	        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//	            // Inflate our menu from a resource file
////	            actionMode.getMenuInflater().inflate(R.menu.map_activity_actions, menu);
//	        	actionMode.setTitle("Locate the target...");
////	            menu.findItem(id)
//	            // Return true so that the action mode is shown
//	            return true;
//	        }
//
//	        @Override
//	        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
//	            // As we do not need to modify the menu before displayed, we return false.
//	            return false;
//	        }
//
//	        @Override
//	        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//	            // Similar to menu handling in Activity.onOptionsItemSelected()
//	            switch (menuItem.getItemId()) {
//	                case R.id.action_done:
//	                    // Some remove functionality
//	                    finish();
//	                	return true;
//	                    
//	            }
//
//	            return false;
//	        }
//
//	        @Override
//	        public void onDestroyActionMode(ActionMode actionMode) {
//	            // Allows you to be notified when the action mode is dismissed
//	        }
//	        
//	    });
		mMap = getMap();
		
		//Elements
		CircleOptions circleOptions = new CircleOptions()
		.center(CENTRALE_PARIS)
		.fillColor(circleColor)
		.strokeWidth(circleStrokeWidth)
		.strokeColor(circleStrokeColor)
		.radius(LocationWatchService.DETECTION_DIST) // In meters
		.visible(false);

		circle = mMap.addCircle(circleOptions);
		
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		mMap.setMyLocationEnabled(true);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocationManager.getLocation(this), 14.0f));
		//		intent.putExtra("point", marker.getPosition());
		//		setResult(RESULT_OK, intent);

		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				if (marker == null){
					marker = mMap.addMarker(new MarkerOptions()
					.position(point)
					.draggable(true));
					circle.setCenter(point);
					circle.setVisible(true);
				}
				else {
					marker.setPosition(point);
					circle.setCenter(point);
				}
				onMarkerMoved();
			}

		});
		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDrag(Marker marker) {
				circle.setCenter(marker.getPosition());
				circle.setFillColor(circleColor);
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				onMarkerMoved();
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				// TODO Auto-generated method stub
			}

		});
	}

	private GoogleMap getMap() {
		return ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	}

	private void onMarkerMoved(){
		circle.setCenter(marker.getPosition());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			animateMarkerOnUpdate();
		}		
		intent.putExtra("point", marker.getPosition());
		setResult(RESULT_OK, intent);
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private void animateMarkerOnUpdate() {
		ValueAnimator vAnimator = new ValueAnimator();
		//	    vAnimator.setRepeatCount(ValueAnimator.INFINITE);
		//	    vAnimator.setRepeatMode(ValueAnimator.RESTART);  /* PULSE */
		vAnimator.setFloatValues((float) circle.getRadius(), (float) circle.getRadius());
		vAnimator.setDuration(1000);
		vAnimator.setEvaluator(new FloatEvaluator());
		vAnimator.setInterpolator(new BounceInterpolator());
		vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float animatedFraction = valueAnimator.getAnimatedFraction();
				// Log.e("", "" + animatedFraction);
				circle.setRadius(animatedFraction * 100);
			}
		});
		vAnimator.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_target, menu);
		return true;
	}

	//	@Override
	//	public void finishActivity(int requestCode) {
	//		
	//		
	//		super.finishActivity(requestCode);
	//	}

}

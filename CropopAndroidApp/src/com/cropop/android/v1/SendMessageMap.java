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
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.animation.BounceInterpolator;

import com.cropop.android.v1.manager.MyLocationManager;
import com.cropop.android.v1.service.NotificationService;
import com.cropop.android.v1.util.ColorUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SendMessageMap extends FragmentActivity {
	private GoogleMap mMap;
	Marker marker;
	static final LatLng CENTRALE_PARIS = new LatLng(48.7638176721, 2.2886595502);
	final Intent intent = new Intent();
	private LocationManager locMgr = null;
	private Criteria crit=new Criteria();
	private Circle circle;
	private int circleColor = ColorUtils.setTransparency(Color.GREEN, 45);
	private int circleStrokeColor = ColorUtils.setTransparency(Color.GREEN, 85);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("Locate the target...");
		super.onCreate(savedInstanceState);

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				GoogleMap googleMap = getMap();
				if(googleMap != null) {

					setContentView(R.layout.send_message_map);

					mMap = getMap();		

					//Elements
					CircleOptions circleOptions = new CircleOptions()
					.fillColor(circleColor)
					.strokeWidth(1.0F)
					.strokeColor(circleStrokeColor)
					.radius(NotificationService.DETECTION_DIST) // In meters
					.visible(false);

					circle = mMap.addCircle(circleOptions);

					locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
					crit.setAccuracy(Criteria.ACCURACY_FINE);
					mMap.setMyLocationEnabled(true);

					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocationManager.getLocation(SendMessageMap.this), 14.0f));
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

				else {
					handler.postDelayed(this, 500);
				}
			}
		}, 500);

	}

	private GoogleMap getMap() {
		FragmentManager sfm = getSupportFragmentManager();
		SupportMapFragment mf = (SupportMapFragment) sfm.findFragmentById(R.id.map);
		return mf.getMap();
	}

	private void onMarkerMoved(){
		circle.setCenter(marker.getPosition());
		//		Log.i("Parse", "Coordinates : "+marker.getPosition());
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

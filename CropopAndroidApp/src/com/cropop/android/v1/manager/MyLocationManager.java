package com.cropop.android.v1.manager;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MyLocationManager {

	public static LatLng getLocation(Context context) {
		double lat = 0;
		double lng = 0;
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (networkLoc != null) {
			Log.i("Parse", "Provider " + provider + " has been selected.");
			lat = (double) (networkLoc.getLatitude());
			lng = (double) (networkLoc.getLongitude());
		}

//		if ( gpsLoc  != null) {
//			Log.i("Parse", "Provider " + provider + " has been selected.");
//			lat = (double) (networkLoc.getLatitude());
//			lng = (double) (networkLoc.getLongitude());
//		}
		return new LatLng(lat, lng);
	}

}

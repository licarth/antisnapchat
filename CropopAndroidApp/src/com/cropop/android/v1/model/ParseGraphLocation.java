package com.cropop.android.v1.model;

import com.facebook.model.GraphLocation;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("FacebookGraphLocation")
public class ParseGraphLocation extends ParseObject {
	
	public ParseGraphLocation() {

	}
	
	public static ParseGraphLocation fromFacebookLocation(GraphLocation facebookGraphLocation) {
		ParseGraphLocation loc = new ParseGraphLocation();
		loc.setStreet(facebookGraphLocation.getStreet());
		loc.setCity(facebookGraphLocation.getCity());
		loc.setState(facebookGraphLocation.getState());
		loc.setCountry(facebookGraphLocation.getCountry());
		loc.setZip(facebookGraphLocation.getZip());
		loc.setGeoPoint(facebookGraphLocation.getLatitude(), facebookGraphLocation.getLongitude());
		return loc;
	}

	public String getStreet() {
		return getString("street");
	}
	
	public void setStreet(String street) {
		if(street != null) put("street", street);
	}
	
	public String getCity() {
		return getString("city");
	}
	
	public void setCity(String city) {
		if(city != null) put("city", city);
	}
	
	public String getState() {
		return getString("state");
	}
	
	public void setState(String state) {
		if(state != null) put("state", state);
	}
	
	public String getCountry() {
		return getString("country");
	}
	
	public void setCountry(String country) {
		if(country != null) put("country", country);
	}
	
	public String getZip() {
		return getString("zip");
	}
	
	public void setZip(String zip) {
		if(zip != null) put("zip", zip);
	}
	
	public ParseGeoPoint getGeoPoint(){
		return getParseGeoPoint("geopoint");
	};
	
	public void setGeoPoint(double latitude, double longitude){
		put("geopoint", new ParseGeoPoint(latitude, longitude));
	};
	
}

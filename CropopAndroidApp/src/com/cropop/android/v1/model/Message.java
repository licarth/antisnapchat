package com.cropop.android.v1.model;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class Message {

	public String objectId;
	private String content;
	private ParseUser exp_user;
	private ParseUser dest_user;
	private ParseGeoPoint target;
	private boolean delivered = false;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ParseUser getExp_user() {
		return exp_user;
	}
	public void setExp_user(ParseUser exp_user) {
		this.exp_user = exp_user;
	}
	public ParseUser getDest_user() {
		return dest_user;
	}
	public void setDest_user(ParseUser dest_user) {
		this.dest_user = dest_user;
	}
	public ParseGeoPoint getTarget() {
		return target;
	}
	public void setTarget(ParseGeoPoint target) {
		this.target = target;
	}
	public boolean isDelivered() {
		return delivered;
	}
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
}

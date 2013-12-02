package com.cropop.android.v1.manager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.facebook.model.GraphUser;
import com.parse.ParseUser;

public class FriendsManager {

	private static List<GraphUser> myFacebookFriends = new ArrayList<GraphUser>();
	private static List<ParseUser> myFacebookParseFriends = new ArrayList<ParseUser>();

	public static List<GraphUser> getMyFacebookFriends() {
		return myFacebookFriends;
	}

	public static void setMyFriends(List<GraphUser> myFriends) {
		FriendsManager.myFacebookFriends = myFriends;
	}
	
	public static List<ParseUser> getMyFacebookParseFriends(){
		return myFacebookParseFriends;
	}
	
	public static String[] getMyFacebookParseFriendsString(){
		final ArrayList<String> list = new ArrayList<String>();
		for (ParseUser f : myFacebookParseFriends) {
			try {
				list.add(f.getJSONObject("profile").getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list.toArray(new String[0]);
	}
	
	public static List<String> getMyFriendsIds() {
		List<String> myFriends = new ArrayList<String>();
		
		for (GraphUser f : getMyFacebookFriends()) {
			myFriends.add(f.getId());
		}
		return myFriends;
	}

	public static void setMyFacebookFriends(List<GraphUser> myFacebookFriends) {
		FriendsManager.myFacebookFriends = myFacebookFriends;
	}

	public static void setMyFacebookParseFriends(
			List<ParseUser> myFacebookParseFriends) {
		FriendsManager.myFacebookParseFriends = myFacebookParseFriends;
	}
	
}

package com.cropop.android.v1.friendsActivities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.cropop.android.v1.R;
import com.cropop.android.v1.manager.FriendsManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CompareWithParseFriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_get_facebook_friends);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereContainedIn("profile.facebookId", FriendsManager.getMyFriendsIds());
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> parseFriends, ParseException e) {
				if (e == null) {
					FriendsManager.setMyFacebookParseFriends(parseFriends);
					Log.d("score", "Retrieved " + parseFriends.size() + " scores");
					finish();
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_facebook_friends, menu);
		return true;
	}

}

package com.cropop.android.v1.friendsActivities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.cropop.android.v1.R;
import com.cropop.android.v1.manager.FriendsManager;

public class GetFacebookFriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_get_facebook_friends);

		Request request = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> friends, Response response) {
				if (friends != null) {
					FriendsManager.setMyFriends(friends);
					Log.i("Parse", friends.size()+" friends found.");
					finish();
				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
//						Log.d(CropopApplication.TAG,
//								"The facebook session was invalidated.");
						
						//TODO Do something !
						//						onLogoutButtonClicked();
					} else {
//						Log.d(CropopApplication.TAG,
//								"Some other error: "
//										+ response.getError()
//										.getErrorMessage());
					}
				}
				
			}
		});
		
		request.executeAsync();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_facebook_friends, menu);
		return true;
	}

}

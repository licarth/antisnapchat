package com.cropop.android.v1;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.cropop.android.v1.R;
import com.cropop.android.v1.manager.FriendsManager;
import com.cropop.android.v1.model.Message;
import com.cropop.android.v1.service.MessageSyncService;
import com.cropop.android.v1.service.DictionaryOpenHelper;
import com.cropop.android.v1.service.LocationWatchService;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

public class MainActivity extends Activity {

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("CropopApplication");
		//		Parse App opens statistics
		ParseAnalytics.trackAppOpened(getIntent());

		startFirstActivity(savedInstanceState);
	}

	private void startApp() {
		PushService.setDefaultPushCallback(this, MainActivity.class);
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put("user", ParseUser.getCurrentUser());
		try {
			installation.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startServices();
		//				sendNotifTests();
		getFriends();
	}

	//	private void sendNotifTests() {
	//				JSONObject data;
	//				try {
	//					data = new JSONObject("{"
	//							+ "\"action\": \"com.cropop.action.NEW_MESSAGE\","
	//							+ "\"name\": \"Vaughn\","
	//							+ "\"newsItem\": \"Man bites dog\"}");
	//
	////					ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
	////					userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
	////
	////					ParseQuery pushQuery = ParseInstallation.getQuery();
	////					pushQuery.whereMatchesQuery("user", userQuery);
	//					ParseQuery pushQuery = ParseInstallation.getQuery();
	//					pushQuery.whereEqualTo("user", ParseUser.getCurrentUser());
	//
	//					ParsePush push = new ParsePush();
	//					push.setQuery(pushQuery);
	//					push.setData(data);
	//					push.sendInBackground();
	//					
	//				} catch (JSONException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}		
	//	}

	private void startServices() {
		startLocationWatchService();
	}

	//	@Override
	protected void startFirstActivity(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			startApp();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		MainActivity.this.progressDialog = ProgressDialog.show(
				MainActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				MainActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(CropopApplication.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(CropopApplication.TAG,
							"User signed up and logged in through Facebook!");
					// Fetch Facebook user info if the session is active
					Session session = ParseFacebookUtils.getSession();
					if (session != null && session.isOpened()) {
						makeMeRequest();
					}
					startApp();
					//					showUserDetailsActivity();
				} else {
					Log.d(CropopApplication.TAG,
							"User logged in through Facebook!");
					startApp();
				}
			}
		});
	}

	protected void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					// Create a JSON object to hold the profile info
					JSONObject userProfile = new JSONObject();
					try {
						// Populate the JSON object
						userProfile.put("facebookId", user.getId());
						userProfile.put("name", user.getName());
						if (user.getLocation().getProperty("name") != null) {
							userProfile.put("location", (String) user
									.getLocation().getProperty("name"));
						}
						if (user.getProperty("gender") != null) {
							userProfile.put("gender",
									(String) user.getProperty("gender"));
						}
						if (user.getBirthday() != null) {
							userProfile.put("birthday",
									user.getBirthday());
						}
						if (user.getProperty("relationship_status") != null) {
							userProfile
							.put("relationship_status",
									(String) user
									.getProperty("relationship_status"));
						}

						// Save the user profile info in a user property
						ParseUser currentUser = ParseUser
								.getCurrentUser();
						currentUser.put("profile", userProfile);
						currentUser.saveInBackground();

						// Show the user info
						//                                                 updateViewsWithProfileInfo();
					} catch (JSONException e) {
						Log.d(CropopApplication.TAG,
								"Error parsing returned user data.");
					}

				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(CropopApplication.TAG,
								"The facebook session was invalidated.");
						//						onLogoutButtonClicked();
					} else {
						Log.d(CropopApplication.TAG,
								"Some other error: "
										+ response.getError()
										.getErrorMessage());
					}
				}
			}
		});
		request.executeAsync();		
	}

	protected void getFriends() {

		//Call Facebook API to get friends...
		MainActivity.this.progressDialog = ProgressDialog.show(
				MainActivity.this, "", "Loading friends...", true);

		Request request = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> friends, Response response) {
				if (friends != null) {
					FriendsManager.setMyFriends(friends);
					Log.i("Parse", friends.size()+" friends found.");
					compareWithParseUsers();


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

	private void displayFriends() {
		FriendsManager.getMyFacebookParseFriends().add(ParseUser.getCurrentUser());
		Intent intent = new Intent(this, FriendsListActivity.class);
		startActivity(intent);
	}

	private void compareWithParseUsers() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereContainedIn("profile.facebookId", FriendsManager.getMyFriendsIds());
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> parseFriends, ParseException e) {
				if (e == null) {
					FriendsManager.setMyFacebookParseFriends(parseFriends);
					Log.d("score", "Retrieved " + parseFriends.size() + " scores");
					displayFriends();
					progressDialog.dismiss();


				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});
	}

	private void startLocationWatchService() {
		Intent locationWatchService = new Intent(getApplicationContext(), LocationWatchService.class);
		getApplicationContext().startService(locationWatchService);
	}
}

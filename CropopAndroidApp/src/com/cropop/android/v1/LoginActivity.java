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
import com.cropop.android.v1.service.CropopService;
import com.cropop.android.v1.service.DictionaryOpenHelper;
import com.cropop.android.v1.service.NotificationService;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setTitle("Cropop");
		
//		startServices();

		//		Intent cropopIntent = new Intent(this, CropopService.class);
		//		startService(cropopIntent);
		//		Intent notifIntent = new Intent(this, NotificationService.class);
		//		startService(notifIntent);
//		DictionaryOpenHelper db = new DictionaryOpenHelper(this);
		//		db.dropTable();
		startFirstActivity(savedInstanceState);

	}

	private void startServices() {
		startNotificationService();
		startCropopService();
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
			getFriends();
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
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(IntegratingFacebookTutorialApplication.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(IntegratingFacebookTutorialApplication.TAG,
							"User signed up and logged in through Facebook!");
					// Fetch Facebook user info if the session is active
					Session session = ParseFacebookUtils.getSession();
					if (session != null && session.isOpened()) {
						makeMeRequest();
					}
					getFriends();
					//					showUserDetailsActivity();
				} else {
					Log.d(IntegratingFacebookTutorialApplication.TAG,
							"User logged in through Facebook!");
					getFriends();
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
						Log.d(IntegratingFacebookTutorialApplication.TAG,
								"Error parsing returned user data.");
					}

				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(IntegratingFacebookTutorialApplication.TAG,
								"The facebook session was invalidated.");
						//						onLogoutButtonClicked();
					} else {
						Log.d(IntegratingFacebookTutorialApplication.TAG,
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
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Loading friends...", true);

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
						//						Log.d(IntegratingFacebookTutorialApplication.TAG,
						//								"The facebook session was invalidated.");

						//TODO Do something !
						//						onLogoutButtonClicked();
					} else {
						//						Log.d(IntegratingFacebookTutorialApplication.TAG,
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
	
	private void startCropopService() {
		Thread t = new Thread(){
			public void run(){
				getApplicationContext().startService(
						new Intent(getApplicationContext(), CropopService.class)
						);
			}
		};
		t.start();
	}

	private void startNotificationService() {
		Thread t = new Thread(){
			public void run(){
				getApplicationContext().startService(
						new Intent(getApplicationContext(), NotificationService.class)
						);
			}
		};
		t.start();
	}
}
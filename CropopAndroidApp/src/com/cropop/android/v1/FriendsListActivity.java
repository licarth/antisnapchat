package com.cropop.android.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cropop.android.v1.R;
import com.cropop.android.v1.manager.FriendsManager;
import com.cropop.android.v1.model.Message;
import com.google.android.gms.internal.p;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FriendsListActivity extends Activity {

	private Message message = new Message();
	public ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Select a friend...");

		message.setExp_user(ParseUser.getCurrentUser());
		setContentView(R.layout.friendslistlayout);
		final List<ParseUser> friends = FriendsManager.getMyFacebookParseFriends();

		final ListView listview = (ListView) findViewById(R.id.friendslist);

		final ArrayList<String> list = new ArrayList<String>();
		for (ParseUser f : friends) {
			try {
				list.add(f.getJSONObject("profile").getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				message.setDest_user(friends.get(position));
				Log.i("Parse", friends.toString());
//				startSendMessageActivity();
				startSelectTargetActivity();
			}
		});

	}

	protected void startSelectTargetActivity() {
		Intent intent = new Intent(this, SendMessageMap.class);
		startActivityForResult(intent, 1);
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	private void startSendMessageActivity() {
		Intent intent = new Intent(this, SendMessageActivity.class);
		startActivityForResult(intent, 2);
		
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			LatLng point = data.getParcelableExtra("point");
			message.setTarget(new ParseGeoPoint(point.latitude, point.longitude));
			startSendMessageActivity();
			break;
		case 2:
			//Back from text
			if(resultCode == RESULT_OK) {
				progressDialog = ProgressDialog.show(
						this, "", "Sending message...", true);
				//Text message sent by user
				String content = data.getStringExtra("content");
				message.setContent(content);
				
				sendMessageAsync();
			} else {
				//TODO Handle errors...
			}
			break;

		default:
			break;
		}
		// Collect data from the intent and use it

	}

	private void sendMessageAsync() {
		ParseObject pMessage = new ParseObject("Message");
		pMessage.put("content", message.getContent());
		pMessage.put("target", message.getTarget());
		// BEWARE ! Don't give full object to avoid modifying it.
		pMessage.put("exp_user", ParseUser.createWithoutData("_User", message.getExp_user().getObjectId()));
		pMessage.put("dest_user", ParseUser.createWithoutData("_User", message.getDest_user().getObjectId()));
		pMessage.put("delivered", false);
		try {
			pMessage.put("exp_name", message.getExp_user().getJSONObject("profile").get("name"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pMessage.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				Log.i("Parse", "Message Sent :!");
				progressDialog.dismiss();
//				ProgressDialog.show(this, "", "Sending message...", true);
			}
		});
	}

	public Message getMessage() {
		return message;
	}

}

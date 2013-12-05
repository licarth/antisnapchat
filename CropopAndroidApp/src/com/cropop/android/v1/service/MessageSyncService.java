package com.cropop.android.v1.service;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cropop.android.v1.model.Message;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MessageSyncService extends Service {

	protected static final String TAG = "MessageSyncService";

	public static long DETECTION_PERIOD = TimeUnit.SECONDS.toMillis(30);

	final Timer timer = new Timer();

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));

	@Override
	public void onCreate() {
		//		super.onCreate();
		//		timer.scheduleAtFixedRate(new TimerTask() {
		//
		//			@Override
		//			public void run() {
		//				Log.i("MessageSyncService", "Service check Run !");
		//				checkForNewMessagesOnline();
		//
		//				//				timer.purge();
		//				//				timer.cancel();
		//			}
		//		}, new Date(System.currentTimeMillis()+2000), DETECTION_PERIOD);
		//		Log.i("MessageSyncService", "Service started : active: ");
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		//Can be called by a push notif. , by the app itself as well, and by the boot receiver as well.
		//Download the messages here and tell location service when done.

		//If it's a push message from Parse.
		executor.execute(new Runnable() {

			@Override
			public void run() {
				downloadNewMessagesFromParse();
				//Send broadcast to say we need to be localized and compare loc to new message(s) loc(s).
				Intent intent = new Intent();
				intent.setAction("com.cropop.action.FORCE_LOCALIZE");
				sendBroadcast(intent);
				stopSelf();
			}
		});
		return START_STICKY;
	}

	protected void downloadNewMessagesFromParse() {
		Log.i("MessageSyncService", "Sending messages request...");
		ParseUser currentUser = ParseUser.getCurrentUser();
		//Parse Query : get all undelivered messages for me.
		// Assume ParseObject myPost was previously created.
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
		query.whereEqualTo("dest_user", currentUser);
		query.whereEqualTo("delivered", false);
		query.include("exp_user");	//Forces to fetch entire exp_user object
		query.include("dest_user");	//Forces to fetch entire exp_user object

		try {
			List<ParseObject> messageList = query.find();
			DictionaryOpenHelper db = new DictionaryOpenHelper(MessageSyncService.this);
			for (ParseObject parseObject : messageList) {
				Message message = new Message();
				message.setObjectId(parseObject.getObjectId());
				message.setContent(parseObject.getString("content"));
				message.setTarget(parseObject.getParseGeoPoint("target"));
				message.setDest_user(parseObject.getParseUser("dest_user"));
				message.setExp_user(parseObject.getParseUser("exp_user"));
				db.addNewMessage(message);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("MessageSyncService", "Service bound.");
		return null;
	}

}

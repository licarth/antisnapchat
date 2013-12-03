package com.cropop.android.v1.service;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cropop.android.v1.model.Message;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CropopService extends Service {

	public static int DETECTION_PERIOD = 2*1000;

	final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		android.os.Debug.waitForDebugger();
		pool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Log.i("CropopService", "Service check Run !");
//				checkForNewMessagesOnline();
			}
		}, 0, DETECTION_PERIOD, TimeUnit.SECONDS);
		Log.i("CropopService", "Service started : active: "+pool.getActiveCount()+"scheduled"+pool.getQueue().size());
//	pool.execute(command);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	protected void checkForNewMessagesOnline() {
		Log.i("CropopService", "Sending messages request...");
		ParseUser currentUser = ParseUser.getCurrentUser();
		//Parse Query : get all undelivered messages for me.
		// Assume ParseObject myPost was previously created.
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
		query.whereEqualTo("dest_user", currentUser);
		query.whereEqualTo("delivered", false);

		try {
			List<ParseObject> messageList = query.find();
			DictionaryOpenHelper db = new DictionaryOpenHelper(CropopService.this);
			for (ParseObject parseObject : messageList) {
				Message message = new Message();
				message.setContent(parseObject.getString("content"));
				message.setTarget(parseObject.getParseGeoPoint("target"));
				message.setDelivered(false);
				message.setDest_user(parseObject.getParseUser("dest_user"));
				message.setExp_user(parseObject.getParseUser("exp_user"));
				message.setObjectId(parseObject.getObjectId());
				db.addNewMessage(message);
			}


		} catch (ParseException e) {

		}


		//		query.findInBackground(new FindCallback<ParseObject>() {
		//			public void done(List<ParseObject> messageList, ParseException e) {
		//				Log.i("ParseQuery", String.format("Found %s messages", messageList.size()));
		//				for (ParseObject parseObject : messageList) {
		//					Message message = new Message();
		//					message.setContent(parseObject.getString("content"));
		//					message.setTarget(parseObject.getParseGeoPoint("target"));
		//					message.setDelivered(false);
		//					message.setDest_user(parseObject.getParseUser("dest_user"));
		//					message.setExp_user(parseObject.getParseUser("exp_user"));
		//					message.setObjectId(parseObject.getObjectId());
		//					DictionaryOpenHelper db = new DictionaryOpenHelper(CropopService.this);
		//					db.addNewMessage(message);
		//				}
		//			}
		//		});


	}



	@Override
	public IBinder onBind(Intent intent) {
		Log.i("CropopService", "Service bound.");
		return null;
	}

}

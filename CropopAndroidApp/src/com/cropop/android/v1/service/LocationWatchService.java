package com.cropop.android.v1.service;

import static com.cropop.android.v1.service.DictionaryOpenHelper.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.cropop.android.v1.R;
import com.cropop.android.v1.ShowMessageActivity;
import com.cropop.android.v1.manager.MyLocationManager;
import com.cropop.android.v1.model.Message;
import com.google.android.gms.internal.al;
import com.google.android.gms.maps.model.LatLng;



public class LocationWatchService extends Service {

	double distance;
	public static int DETECTION_DIST = 100; //En metres
	public static long DETECTION_PERIOD = TimeUnit.SECONDS.toMillis(60);
	public BroadcastReceiver forceLocalizeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
				forceDetection();
		}
	};
	
	private Timer timer = new Timer();

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(forceLocalizeReceiver, new IntentFilter("com.cropop.action.FORCE_LOCALIZE"));
		Log.i("MessageSyncService", "Service started.");

		timer.scheduleAtFixedRate(new TimerTask() {
			synchronized public void run() {
				Log.i("LocationWatchService", "Notification Service Run !");
				alertUser();
			}
		}, new Date(System.currentTimeMillis() + 3000), DETECTION_PERIOD);
	}
	
	public void forceDetection(){
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				alertUser();
			}
		}, 0);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	protected void alertUser() {

		Cursor c = null;
		int messagesCount = 0;
		int secretMessages = 0;
		int messagesToBeNotified = 0;

		try {
			LatLng pos = MyLocationManager.getLocation(LocationWatchService.this);

			DictionaryOpenHelper db = new DictionaryOpenHelper(LocationWatchService.this);
			c = db.getUnNotifiedMessages(); 
			while (c.moveToNext()) {
				double lat = c.getDouble(c.getColumnIndex(TARGET_LAT));
				double lng = c.getDouble(c.getColumnIndex(TARGET_LNG));
				LatLng target = new LatLng(lat, lng);
				distance = getDistanceBetween(pos, target);

				if (distance * 1000 <= DETECTION_DIST){
					messagesToBeNotified++;
					//Notify
					Message m = new Message();
					m = db.createMessageFromCursor(c);

					//Notify user !
					notifyUser(m);
					db.markAsNotified(m);
				}
				else {
					//Store for further notification.
					messagesCount++;
					secretMessages++;
				}
			}
		} finally {
			c.close();
		}
		Log.i("MessageNotification", String.format("You've got %s messages, (%s secret and %s new)",messagesCount, secretMessages, messagesToBeNotified));
	}

	private double getDistanceBetween(LatLng p1, LatLng p2) {
		double R = 6371; // earth's mean radius in km
		double dLat  = Math.toRadians(p2.latitude - p1.latitude);
		double dLong = Math.toRadians(p2.longitude - p1.longitude);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(p1.latitude)) * Math.cos(Math.toRadians(p2.latitude)) * Math.sin(dLong/2) * Math.sin(dLong/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;

	}

	private void notifyUser(Message message) {
		// Set the icon, scrolling text and timestamp
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Builder mBuilder = new NotificationCompat.Builder(this)
		.setContentTitle("Dist: " +((int) (distance * 1000))+" m" +", Exp:"+ ((message.getExp_user() != null) ? message.getExp_user().getUsername() :"Unknown"))
		.setContentText(message.getContent())
		.setSmallIcon(R.drawable.nopic);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ShowMessageActivity.class);

		resultIntent.putExtra("messageId", message.objectId);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ShowMessageActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		// mId allows you to update the notification later on.
		notificationManager.notify(message.getObjectId().hashCode(), mBuilder.build());
	}


	@Override
	public IBinder onBind(Intent intent) {
		Log.i("LocationWatchService", "Service bound.");
		return null;
	}

}

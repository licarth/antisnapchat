package com.cropop.android.v1.service;

import static com.cropop.android.v1.service.DictionaryOpenHelper.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.cropop.android.v1.R;
import com.cropop.android.v1.ShowMessageActivity;
import com.cropop.android.v1.manager.MyLocationManager;
import com.cropop.android.v1.model.Message;

public class NotificationService extends Service {
	
	double distance;
	public static int DETECTION_DIST = 40; //En metres
	public static int DETECTION_PERIOD = 10*1000;

	public Timer timer = new Timer();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("CropopService", "Service started.");


//		timer.schedule(new TimerTask() {
//
//			synchronized public void run() {
//				Log.i("NotificationService", "Notification Service Run !");
//				alertUser();
//			}
//
//		}, new Date(System.currentTimeMillis() + 10000));
//		
		timer.scheduleAtFixedRate(new TimerTask() {

			synchronized public void run() {
				Log.i("NotificationService", "Notification Service Run !");
				alertUser();
			}

		}, new Date(System.currentTimeMillis() + 3000), DETECTION_PERIOD);

		return START_STICKY;

	}
	
	
	protected void alertUser() {

		Cursor c = null;
		float[] dists = new float[3];

		try {
			LatLng pos = MyLocationManager.getLocation(NotificationService.this);

			DictionaryOpenHelper db = new DictionaryOpenHelper(NotificationService.this);
			c = db.getUnDeliveredMessages(); 
			while (c.moveToNext()) {
				double lat = c.getDouble(c.getColumnIndex(TARGET_LAT));
				double lng = c.getDouble(c.getColumnIndex(TARGET_LNG));
				LatLng target = new LatLng(lat, lng);
//				Location.distanceBetween(pos.latitude, pos.longitude, target.latitude, target.longitude, dists);
//				distance = Math.acos(Math.sin(pos.latitude)*Math.sin(target.latitude) + 
//				                  Math.cos(pos.latitude)*Math.cos(target.latitude) *
//				                  Math.cos(target.longitude-pos.longitude)) * R;
				distance = getDistanceBetween(pos, target);
				
				if (distance * 1000 <= DETECTION_DIST){
					//Notify
					Log.i("MessageNotification", "You've got one NEW MESSAGE !");
					Message message = new Message();
//					message.setObjectId(c.getString(c.getColumnIndex(OBJECT_ID)));
//					message.setContent(c.getString(c.getColumnIndex(CONTENT)));
//					message.setTarget(new ParseGeoPoint(c.getDouble(c.getColumnIndex(TARGET_LAT)), c.getDouble(c.getColumnIndex(TARGET_LNG))));
//					message.setDelivered(false);
//					
//					ParseUser dest = new ParseUser();
//					dest.setUsername(c.getString(c.getColumnIndex(DEST_USER)));
//					message.setDest_user(dest);
//					
//					ParseUser exp = new ParseUser();
//					dest.setUsername(c.getString(c.getColumnIndex(DEST_USER)));
//					message.setDest_user(exp);
					message = db.createMessageFromCursor(c);
					
					//Notify user !
					db.markAsRead(message);
					notifyUser(message);
				}

				else {
					//Store for further notification.
					Log.i("MessageNotification", "You've got one message but you SHOULD NOT KNOW IT !");
					
					
				}
			}
		} finally {
			c.close();
		}
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
	
	@SuppressLint("NewApi")
	private void notifyUser(Message message) {
		    // Set the icon, scrolling text and timestamp
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 Builder mBuilder = new Notification.Builder(this)
         .setContentTitle("Dist: " +((int) distance * 1000)+" m" +", Exp:"+ ((message.getExp_user() != null) ? message.getExp_user().getUsername() :"Unknown"))
         .setContentText(message.getContent())
         .setSmallIcon(R.drawable.nopic);
		 
		// Creates an explicit intent for an Activity in your app
		 Intent resultIntent = new Intent(this, ShowMessageActivity.class);
		 
		 resultIntent.putExtra("messageId", message.objectId);

		 // The stack builder object will contain an artificial back stack for the
		 // started Activity.
		 // This ensures that navigating backward from the Activity leads out of
		 // your application to the Home screen.
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
		Log.i("NotificationService", "Service bound.");
		return null;
	}
	
}

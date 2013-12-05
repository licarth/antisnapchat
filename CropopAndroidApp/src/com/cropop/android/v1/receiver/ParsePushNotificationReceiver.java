package com.cropop.android.v1.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cropop.android.v1.service.MessageSyncService;

/**
 * @author thomas
 *
 *	Receiver that receives new messages broadcasts for Cropop,
 *	even when Cropop app & services are not up and running...
 *
 *
 */
public class ParsePushNotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Get action type
		String action = intent.getAction();
		if (action.equals("com.cropop.action.NEW_MESSAGE")){
			//Pass message to MessageSyncActivity to download it.
			JSONObject json;
			try {
				json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				Log.i("PUSH", json.getString("expName"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent messageSyncService = new Intent(context, MessageSyncService.class);
			messageSyncService.putExtras(intent.getExtras());
			context.startService(messageSyncService);
		}
	}

}

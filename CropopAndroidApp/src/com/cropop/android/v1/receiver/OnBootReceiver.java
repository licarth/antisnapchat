package com.cropop.android.v1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cropop.android.v1.service.MessageSyncService;
import com.cropop.android.v1.service.LocationWatchService;

/**
 * Receiver that starts the services at boot time.
 * 
 * @author thomas
 *
 */
public class OnBootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		//Start services.
        Intent locationWatchService = new Intent(context, LocationWatchService.class);
        context.startService(locationWatchService);
//        Intent cropopIntent = new Intent(context, MessageSyncService.class);
//        context.startService(cropopIntent);
	}
}

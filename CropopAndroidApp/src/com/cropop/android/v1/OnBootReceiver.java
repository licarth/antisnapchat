package com.cropop.android.v1;

import android.content.Context;
import android.content.Intent;

import com.cropop.android.v1.service.BootCompletedReceiver;
import com.cropop.android.v1.service.CropopService;
import com.cropop.android.v1.service.NotificationService;

public class OnBootReceiver extends BootCompletedReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Run services !
        Intent notifIntent = new Intent(context, NotificationService.class);
        context.startService(notifIntent);
        Intent cropopIntent = new Intent(context, CropopService.class);
        context.startService(cropopIntent);
		super.onReceive(context, arg1);
	}
}

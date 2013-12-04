package com.cropop.android.v1;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class CropopApplication extends Application {

	public static final String TAG = "CropopApplication";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "ngjj0L1IKjFUMEQIuD0PRCrzLyIWXMG5NFxCn6U5", "VTIydiAAiKTe4AX9HWkwnilyuXfjW1TMz8Um2cca");

		//	Parse Facebook Utils
		ParseFacebookUtils.initialize(getString(R.string.app_id));
		//	Parse Push

	}

}

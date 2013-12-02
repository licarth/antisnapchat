package com.cropop.android.v1;

import android.app.Application;
import com.cropop.android.v1.R;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class IntegratingFacebookTutorialApplication extends Application {

	static final String TAG = "Antisnapchat";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "ngjj0L1IKjFUMEQIuD0PRCrzLyIWXMG5NFxCn6U5",
				"VTIydiAAiKTe4AX9HWkwnilyuXfjW1TMz8Um2cca");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

	}

}

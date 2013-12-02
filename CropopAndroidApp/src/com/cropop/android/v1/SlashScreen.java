package com.cropop.android.v1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.cropop.android.v1.R;

public class SlashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slash_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slash_screen, menu);
		return true;
	}

}

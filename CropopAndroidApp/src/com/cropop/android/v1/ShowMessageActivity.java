package com.cropop.android.v1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.cropop.android.v1.R;
import com.cropop.android.v1.model.Message;
import com.cropop.android.v1.service.DictionaryOpenHelper;

public class ShowMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_message);
		DictionaryOpenHelper db = new DictionaryOpenHelper(this);
		Message m = db.getMessage(getIntent().getStringExtra("messageId"));
		TextView textView = (TextView) findViewById(R.id.messageContentDisplay);
		textView.setText(m.getContent());
		db.markAsRead(m);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_message, menu);
		return true;
	}

}

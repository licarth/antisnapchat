package com.cropop.android.v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import com.cropop.android.v1.R;

public class SendMessageActivity extends Activity {

	private Button sendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Enter your message ...");
		setContentView(R.layout.activity_send_message);
		final Intent intent = new Intent();

		sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.putExtra("content", ((MultiAutoCompleteTextView) findViewById(R.id.messageContent)).getText().toString());
//				intent.putExtra("geopoint", "This is a geoloc.");
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_message_avtivity, menu);
		return true;
	} 

}

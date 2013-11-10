package com.squirrelbox.ioio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.squirrelbox.ioio.R;

public class BoxMainActivity extends Activity {

	private Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("Main", "OnCreate");
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button_IOIO);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BoxMainActivity.this, OpenLockActivity.class);
				startActivity(intent);
			}
		});
	}

}
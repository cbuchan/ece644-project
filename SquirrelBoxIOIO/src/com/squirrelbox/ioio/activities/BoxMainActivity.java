package com.squirrelbox.ioio.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.base.data.model.Box;
import com.squirrelbox.ioio.R;
import com.squirrelbox.ioio.SquirrelBoxIOIOApplication;

public class BoxMainActivity extends Activity {

	public final static String TAG = BoxMainActivity.class.getName();

	private SquirrelBoxIOIOApplication application;
	private NetworkProvider networkProvider;
	private Button openLockButton;
	private Button relinquishButton;
	private Button refreshButton;

	private TextView boxId;
	private TextView reserved;
	private TextView keyHolder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("Main", "OnCreate");
		setContentView(R.layout.activity_main);

		application = (SquirrelBoxIOIOApplication) getApplication();
		networkProvider = application.getNetworkProvider();

		openLockButton = (Button) findViewById(R.id.button_IOIO);
		relinquishButton = (Button) findViewById(R.id.button_relinquish);
		refreshButton = (Button) findViewById(R.id.button_refresh);

		boxId = (TextView) findViewById(R.id.box_id);
		reserved = (TextView) findViewById(R.id.reserved_status);
		keyHolder = (TextView) findViewById(R.id.key_holder);

		// Check box status
		refreshStatus();

		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshStatus();
			}
		});

		openLockButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BoxMainActivity.this, HelloIOIOActivity.class);
				startActivity(intent);
			}
		});

		relinquishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				networkProvider.postBoxRelinquishToNetwork(1, new DataCallbackHandler() {
					@Override
					public void onRelinquishSuccess() {
						Log.i(TAG, "Relinquish successful");
						refreshStatus();
					}
				});
			}
		});
	}

	private void refreshStatus() {
		// Check box status
		networkProvider.getBoxSelfStatusFromNetwork(new DataCallbackHandler() {
			@Override
			public void onBoxSelfStatusSuccess(Box box) {
				boxId.setText("" + box.getId());
				reserved.setText("" + box.getStatus());
				keyHolder.setText("" + box.getKeyHolder().getUsername());
			}
		});
	}

}
package com.squirrelbox.user.activities;

import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.user.R;
import com.squirrelbox.user.SquirrelBoxUserApplication;

public class MainActivity extends Activity implements CreateNdefMessageCallback {

	public final static String TAG = MainActivity.class.getName();

	private SquirrelBoxUserApplication application;
	private NetworkProvider networkProvider;

	NfcAdapter mNfcAdapter;
	TextView boxStatusText;
	Button reservationButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		application = (SquirrelBoxUserApplication) getApplication();
		networkProvider = application.getNetworkProvider();

		boxStatusText = (TextView) findViewById(R.id.text_box_status);
		reservationButton = (Button) findViewById(R.id.button_reservation);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// Register callback
		mNfcAdapter.setNdefPushMessageCallback(this, this);

		// Check box status
		networkProvider.getBoxStatusFromNetwork(new DataCallbackHandler() {
			@Override
			public void onBoxStatusSuccess() {
				boxStatusText.setText("Successfully checked status");
			}
		});

		reservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				networkProvider.postReservationToNetwork("steve", "book", new DataCallbackHandler() {
					@Override
					public void onReservationSuccess() {
						boxStatusText.setText("Successfully posted reservation");
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_logout:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
			prefs.edit().clear().apply();
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		JSONObject testObject = new JSONObject();
		try {
			testObject.put("text", "hello world!");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String text = (testObject.toString());
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.squirrelbox.user",
				text.getBytes()) });
		return msg;

	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}
}

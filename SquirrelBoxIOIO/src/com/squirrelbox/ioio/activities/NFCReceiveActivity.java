package com.squirrelbox.ioio.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.ioio.R;
import com.squirrelbox.ioio.SquirrelBoxIOIOApplication;

public class NFCReceiveActivity extends Activity {
	public final static String TAG = NFCReceiveActivity.class.getName();

	NfcAdapter mNfcAdapter;
	TextView box_status;
	TextView user_id;
	TextView nfc_message;
	SquirrelBoxIOIOApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SquirrelBoxIOIOApplication) getApplication();
		
		setContentView(R.layout.nfc);
		box_status = (TextView) findViewById(R.id.box_status);
		user_id = (TextView) findViewById(R.id.user_id);
		nfc_message = (TextView) findViewById(R.id.NFC_message);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.e(TAG, "Beam Received!");
			processIntent(getIntent());
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String message = new String(msg.getRecords()[0].getPayload());
		JSONObject json_message;

		nfc_message.setText(message);
		try {
			json_message = new JSONObject(message);
			nfc_message.setText(json_message.optString("text"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// / TODO: Verify credentials with server
		NetworkProvider n = application.getNetworkProvider();
		
		/// TODO: Open box if they're valid
	    Intent open_lock_intent = new Intent(this, OpenLockActivity.class);
	    startActivity(open_lock_intent);
	}
}

package com.squirrelbox.ioio;

import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.ioio.R;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class NFCReceiveActivity extends Activity implements CreateNdefMessageCallback {
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
		
		setContentView(R.layout.activity_main);
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
		// Register callback
		mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String text = ("Beam me up, Android!\n\n" + "Beam Time: " + System.currentTimeMillis());
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.squirrelbox.user",
				text.getBytes()) });
		return msg;

	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
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
		try {
			json_message = new JSONObject(message);
			nfc_message.setText(json_message.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/// TODO: Verify credentials with server
		NetworkProvider n = application.getNetworkProvider();
		
		/// TODO: Open box if they're valid
		
	}
}

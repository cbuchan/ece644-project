package com.squirrelbox.user.activities;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.base.data.model.Box;
import com.squirrelbox.base.data.model.User;
import com.squirrelbox.base.util.Keys;
import com.squirrelbox.user.R;
import com.squirrelbox.user.SquirrelBoxUserApplication;

public class MainActivity extends Activity implements CreateNdefMessageCallback {

	public final static String TAG = MainActivity.class.getName();

	private SquirrelBoxUserApplication application;
	private NetworkProvider networkProvider;

	NfcAdapter mNfcAdapter;
	TextView boxStatusText;
	Button reservationButton;
	Button refreshButton;
	ListView boxStatusListView;
	BoxListAdapter boxStatusAdapter;
	Spinner userSpinner;
	UserSpinnerAdapter userSpinnerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		application = (SquirrelBoxUserApplication) getApplication();
		networkProvider = application.getNetworkProvider();

		boxStatusText = (TextView) findViewById(R.id.text_box_status);
		reservationButton = (Button) findViewById(R.id.button_reservation);
		refreshButton = (Button) findViewById(R.id.button_refresh);

		userSpinner = (Spinner) findViewById(R.id.spinner_user_list);
		userSpinnerAdapter = new UserSpinnerAdapter(this, new ArrayList<User>());
		userSpinner.setAdapter(userSpinnerAdapter);

		boxStatusListView = (ListView) findViewById(R.id.box_status_list);
		boxStatusAdapter = new BoxListAdapter(this, new ArrayList<Box>());
		boxStatusListView.setAdapter(boxStatusAdapter);

		PackageManager packageManager = application.getPackageManager();

		if (packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
			// yes
			Log.i(TAG, "This device has NFC!");

			// Check for available NFC Adapter
			mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if (mNfcAdapter == null) {
				Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			// Register callback
			mNfcAdapter.setNdefPushMessageCallback(this, this);

		} else {
			// no
			Log.i(TAG, "This device has no NFC!");
		}

		// Check box status
		refreshBoxStatus();

		// Get users
		networkProvider.getUsersFromNetwork(new DataCallbackHandler() {
			@Override
			public void onUserListSuccess(ArrayList<User> users) {
				userSpinnerAdapter.addAll(users);
				userSpinnerAdapter.notifyDataSetChanged();
			}
		});

		reservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int receiverId = userSpinnerAdapter.getItem(userSpinner.getSelectedItemPosition()).getUserId();
				networkProvider.postReservationToNetwork(1, receiverId, "book", new DataCallbackHandler() {
					@Override
					public void onReservationSuccess() {
						boxStatusText.setText("Successfully posted reservation");
						refreshBoxStatus();
					}
				});
			}
		});

		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshBoxStatus();
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
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
			testObject.put(Keys.AUTH_TOKEN, prefs.getString(Keys.AUTH_TOKEN, ""));
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

	private void refreshBoxStatus() {
		// Check box status
		networkProvider.getBoxStatusFromNetwork(new DataCallbackHandler() {
			@Override
			public void onBoxStatusSuccess(ArrayList<Box> boxes) {
				boxStatusAdapter.clear();
				boxStatusAdapter.addAll(boxes);
				boxStatusAdapter.notifyDataSetChanged();
				boxStatusText.setText("Successfully checked status");
			}
		});
	}

	class BoxListAdapter extends ArrayAdapter<Box> {

		public void addAll(ArrayList<Box> boxes) {
			for (Box box : boxes) {
				this.add(box);
			}
		}

		public BoxListAdapter(Context context, List<Box> objects) {
			super(context, R.layout.list_item_box, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.list_item_box, parent, false);
			}

			Box box = getItem(position);

			TextView categoryName = (TextView) convertView.findViewById(R.id.box_label);
			ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);

			categoryName.setText("Box ID: " + box.getId());

			if (box.getStatus().equals("reserved")) {
				if (box.getPermission().equals("granted")) {
					statusIcon.setImageResource(R.drawable.blue);
				} else if (box.getPermission().equals("denied")) {
					statusIcon.setImageResource(R.drawable.red);
				}
			} else if (box.getStatus().equals("available")) {
				statusIcon.setImageResource(R.drawable.green);
			}

			return convertView;
		}
	}

	class UserSpinnerAdapter extends ArrayAdapter<User> implements SpinnerAdapter {
		private List<User> users;

		public void addAll(ArrayList<User> users) {
			for (User user : users) {
				this.add(user);
			}
		}

		public UserSpinnerAdapter(Context context, List<User> users) {
			super(context, R.layout.list_item_user, users);
			this.users = users;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.list_item_user, parent, false);
			}

			User user = getItem(position);
			TextView userName = (TextView) convertView.findViewById(R.id.user_name);
			userName.setText(user.getUsername());

			return convertView;
		}

		@Override
		public User getItem(int position) {
			return this.users.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.list_item_user, parent, false);
			}

			User user = getItem(position);
			TextView userName = (TextView) convertView.findViewById(R.id.user_name);
			userName.setText(user.getUsername());

			return convertView;
		}
	}
}

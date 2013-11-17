package com.squirrelbox.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.base.util.Keys;
import com.squirrelbox.user.R;
import com.squirrelbox.user.SquirrelBoxUserApplication;

public class LoginActivity extends Activity {

	public final static String TAG = LoginActivity.class.getName();

	private SquirrelBoxUserApplication application;
	private NetworkProvider networkProvider;

	private Button loginButton;
	private Button registerButton;

	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		application = (SquirrelBoxUserApplication) getApplication();
		networkProvider = application.getNetworkProvider();

		loginButton = (Button) findViewById(R.id.button_login);
		registerButton = (Button) findViewById(R.id.button_register);

		usernameEditText = (EditText) findViewById(R.id.edit_email);
		passwordEditText = (EditText) findViewById(R.id.edit_password);

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendLoginRequest();
			}
		});

		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendRegisterRequest();
			}
		});

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
		if (prefs.contains(Keys.AUTH_TOKEN)) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	private void sendLoginRequest() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (!username.isEmpty() && !password.isEmpty()) {
			networkProvider.postLoginRequestToNetwork(username, password, new DataCallbackHandler() {
				@Override
				public void onTokenSuccess(Token token) {
					Log.e(TAG, "Token: " + token.getAuthToken());

					token.getAuthToken();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
					prefs.edit().putString(Keys.AUTH_TOKEN, token.getAuthToken()).apply();

					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}

				@Override
				public void onFailure(String errorMessage) {
					Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(this, "Username or Password blank!", Toast.LENGTH_SHORT).show();
		}

	}

	private void sendRegisterRequest() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (!username.isEmpty() && !password.isEmpty()) {

			networkProvider.postUserToNetwork(username, password, new DataCallbackHandler() {
				@Override
				public void onTokenSuccess(Token token) {
					// Do something

					token.getAuthToken();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
					prefs.edit().putString(Keys.AUTH_TOKEN, token.getAuthToken()).apply();

					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}

				@Override
				public void onFailure(String errorMessage) {
					Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(LoginActivity.this, "Username or Password blank!", Toast.LENGTH_SHORT).show();
		}

	}
}

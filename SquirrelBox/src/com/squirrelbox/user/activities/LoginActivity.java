package com.squirrelbox.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.squirrelbox.base.api.NetworkProvider;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.user.R;
import com.squirrelbox.user.SquirrelBoxUserApplication;

public class LoginActivity extends Activity {

	private SquirrelBoxUserApplication application;
	private NetworkProvider networkProvider;

	private Button loginButton;
	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		application = (SquirrelBoxUserApplication) getApplication();
		networkProvider = application.getNetworkProvider();

		loginButton = (Button) findViewById(R.id.button_login);
		usernameEditText = (EditText) findViewById(R.id.edit_email);
		passwordEditText = (EditText) findViewById(R.id.edit_password);

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendLoginRequest();
			}
		});
	}

	private void sendLoginRequest() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);

		// networkProvider.postLoginRequestToNetwork(username, password, new
		// DataCallbackHandler() {
		// @Override
		// public void onTokenSuccess(Token token) {
		// // Do something
		//
		// Intent intent = new Intent(LoginActivity.this, BeamActivity.class);
		// startActivity(intent);
		// }
		// });
	}

}

package com.squirrelbox.base.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.squirrelbox.base.SquirrelBoxBaseApplication;
import com.squirrelbox.base.data.DataCallbackHandler;
import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.base.data.model.User;

public class NetworkProvider {

	private static final String TAG = NetworkProvider.class.getSimpleName();
	public static final String API_VERSION = "/api/v1";
	private Context context;
	private JSONBuilder jsonBuilder;

	public NetworkProvider(SquirrelBoxBaseApplication application) {
		this.context = application.getApplicationContext();
		jsonBuilder = new JSONBuilder();

		SquirrelBoxRestClient.setTimeout(30000);
	}

	public void clearCookieCache() {
		SquirrelBoxRestClient.setCookieStore(new PersistentCookieStore(context));
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * GET REQUESTS
	 *********************************************************************/
	public void getUserFromNetwork(int userId, final DataCallbackHandler dataHandler) {
		SquirrelBoxRestClient.authGet(API_VERSION + "/users/" + userId + ".json", context, null,
				new SquirrelBoxJsonResponseHandler(context, dataHandler) {
					@Override
					public void processResponse(JSONObject rawResponse) throws JSONException {
						User user = JSONParser.parseUser(rawResponse);
						dataHandler.onUserSuccess(user);
					}
				});
	}

	/*********************************************************************
	 * POST REQUESTS
	 *********************************************************************/

	public void postLoginRequestToNetwork(String username, String password, final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);
		postTokenRequest(params, dataHandler);
	}

	public void postFacebookLoginRequestToNetwork(String facebookToken, final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put("fb_token", facebookToken);
		postTokenRequest(params, dataHandler);
	}

	private void postTokenRequest(RequestParams params, final DataCallbackHandler dataHandler) {
		SquirrelBoxRestClient.post(API_VERSION + "/tokens", params, new SquirrelBoxJsonResponseHandler(context, dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				Token token;
				token = JSONParser.parseToken(rawResponse);
				dataHandler.onTokenSuccess(token);
			}
		});
	}

	// Creates a new user
	public void postUserToNetwork(User user, final DataCallbackHandler dataHandler) {

		try {
			JSONObject userRequestJson = jsonBuilder.createUserJSON(user);
			SquirrelBoxRestClient.post(API_VERSION + "/users.json", context, userRequestJson, "application/json",
					new SquirrelBoxJsonResponseHandler(context, dataHandler, "user") {
						@Override
						public void processResponse(JSONObject rawResponse) throws JSONException {
							Log.d(TAG, "" + rawResponse);
							Token token = JSONParser.parseToken(rawResponse);
							dataHandler.onTokenSuccess(token);
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************
	 * PUT REQUESTS
	 *********************************************************************/
	
	/*********************************************************************
	 * HELPER FUNCTIONS
	 *********************************************************************/

	private static ByteArrayInputStream convertBitmap(Bitmap photo) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		photo.compress(CompressFormat.PNG, 100, bos);
		byte[] data = bos.toByteArray();
		photo.recycle();
		return new ByteArrayInputStream(data);
	}
}

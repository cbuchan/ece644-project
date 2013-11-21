package com.squirrelbox.base.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
import com.squirrelbox.base.data.model.Box;
import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.base.data.model.User;
import com.squirrelbox.base.util.Keys;

public class NetworkProvider {

	private static final String TAG = NetworkProvider.class.getSimpleName();
	public static final String API_VERSION = "";
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

	public void getBoxStatusFromNetwork(final DataCallbackHandler dataHandler) {
		SquirrelBoxRestClient.authPost(API_VERSION + "/box_status", context, null, new SquirrelBoxJsonResponseHandler(
				context, dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				// Parse box status
				ArrayList<Box> boxes = JSONParser.parseBoxes(rawResponse);
				dataHandler.onBoxStatusSuccess(boxes);
			}
		});
	}

	public void getBoxSelfStatusFromNetwork(final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put(Keys.AUTH_TOKEN, "1");
		SquirrelBoxRestClient.post(API_VERSION + "/box_self_status", params, new SquirrelBoxJsonResponseHandler(
				context, dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				// Parse box status
				Log.e(TAG, rawResponse.toString());
				Box box = JSONParser.parseBox(rawResponse);
				dataHandler.onBoxSelfStatusSuccess(box);
			}
		});
	}

	public void getUsersFromNetwork(final DataCallbackHandler dataHandler) {
		SquirrelBoxRestClient.post(API_VERSION + "/list_users", null, new SquirrelBoxJsonResponseHandler(context,
				dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				// Parse box status
				ArrayList<User> users = JSONParser.parseUsers(rawResponse);
				dataHandler.onUserListSuccess(users);
			}
		});
	}

	/*********************************************************************
	 * POST REQUESTS
	 *********************************************************************/

	public void postLoginRequestToNetwork(String username, String password, final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);

		SquirrelBoxRestClient.post(API_VERSION + "/authenticate", params, new SquirrelBoxJsonResponseHandler(context,
				dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				Token token;
				token = JSONParser.parseToken(rawResponse);
				if (token.getAuthToken() != null) {
					dataHandler.onTokenSuccess(token);
				} else {
					dataHandler.onFailure("Unable to get auth token");
				}
			}
		});
	}

	// Creates a new user
	public void postUserToNetwork(String username, String password, final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);

		SquirrelBoxRestClient.post(API_VERSION + "/register", params, new SquirrelBoxJsonResponseHandler(context,
				dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				Token token;
				token = JSONParser.parseToken(rawResponse);
				if (token.getAuthToken() != null) {
					dataHandler.onTokenSuccess(token);
				} else {
					dataHandler.onFailure("Unable to get auth token");
				}
			}
		});

	}

	public void postReservationToNetwork(int boxId, int receiverId, String itemDescription,
			final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		Log.i(TAG, "id " + receiverId);
		params.put("box_id", "" + boxId);
		params.put("receiver_id", "" + receiverId);
		params.put("item_description", itemDescription);

		SquirrelBoxRestClient.authPost(API_VERSION + "/make_reservation", context, params,
				new SquirrelBoxJsonResponseHandler(context, dataHandler) {
					@Override
					public void processResponse(JSONObject rawResponse) throws JSONException {
						// Parse reservation success
						Log.i(TAG, rawResponse.toString());
						dataHandler.onReservationSuccess();
					}
				});
	}

	public void postRelinquishToNetwork(final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		SquirrelBoxRestClient.authPost(API_VERSION + "/relinquish", context, params,
				new SquirrelBoxJsonResponseHandler(context, dataHandler) {
					@Override
					public void processResponse(JSONObject rawResponse) throws JSONException {
						// Parse relinquish success
						dataHandler.onRelinquishSuccess();
					}
				});
	}

	public void postBoxRelinquishToNetwork(int boxId, final DataCallbackHandler dataHandler) {
		RequestParams params = new RequestParams();
		params.put("boxid", "" + boxId);
		SquirrelBoxRestClient.post(API_VERSION + "/box_relinquish", params, new SquirrelBoxJsonResponseHandler(context,
				dataHandler) {
			@Override
			public void processResponse(JSONObject rawResponse) throws JSONException {
				// Parse relinquish success
				dataHandler.onRelinquishSuccess();
			}
		});
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

package com.squirrelbox.base.api;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squirrelbox.base.data.DataCallbackHandler;

public class SquirrelBoxJsonResponseHandler extends JsonHttpResponseHandler {

	private static String TAG = SquirrelBoxJsonResponseHandler.class.getSimpleName();

	private DataCallbackHandler dataHandler;
	private Context context;
	private String parentObject;

	public SquirrelBoxJsonResponseHandler(Context context, DataCallbackHandler dataHandler) {
		this.dataHandler = dataHandler;
		this.context = context;
	}

	public SquirrelBoxJsonResponseHandler(Context context, DataCallbackHandler dataHandler, String parentObject) {
		this.dataHandler = dataHandler;
		this.context = context;
		this.parentObject = parentObject;
	}

	@Override
	public void onSuccess(JSONObject rawResponse) {
		try {
			String errorMessage;

			if (parentObject != null && rawResponse.has(parentObject)) {
				errorMessage = JSONParser.parseErrors(rawResponse.getJSONObject(parentObject));
			} else {
				errorMessage = JSONParser.parseErrors(rawResponse);
			}

			if (errorMessage != null) {
				Log.w(TAG, rawResponse.toString());
				dataHandler.onFailure(errorMessage);
				toast(errorMessage);
			} else {
				processResponse(rawResponse);
			}
		} catch (Exception e) {
			Log.w(TAG, rawResponse.toString());

			String errorMessage = "Error: failed to parse response.";
			dataHandler.onFailure(errorMessage);
			toast(errorMessage);
			e.printStackTrace();
		}

	}

	@Override
	public void handleFailureMessage(Throwable e, String responseBody) {
		Log.e(TAG, "Failure message: " + responseBody);
		e.printStackTrace();

		if (responseBody != null) {
			// Try to parse errors
			try {
				JSONObject response = new JSONObject(responseBody);
				String errorMessage = JSONParser.parseErrors(response);

				if (errorMessage != null) {
					dataHandler.onFailure(errorMessage);
				} else if (response.has("error")) {
					errorMessage = "Error: " + response.getString("error");
					dataHandler.onFailure(errorMessage);
					toast(errorMessage);
				} else {
					dataHandler.onFailure("Error: failed network request.");
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
				dataHandler.onFailure("Error: failed to parse errors.");
			}
		} else {
			dataHandler.onFailure("Error: no response.");
		}

	}

	public void processResponse(JSONObject rawResponse) throws JSONException, ParseException {
	};

	public void toast(String errorMessage) {
		Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
	}
}

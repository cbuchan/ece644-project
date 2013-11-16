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

	public void processResponse(JSONObject rawResponse) throws JSONException, ParseException {
	};

	public void toast(String errorMessage) {
		Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
	}
}

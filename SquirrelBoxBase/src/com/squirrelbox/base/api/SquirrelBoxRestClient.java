package com.squirrelbox.base.api;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.squirrelbox.base.SquirrelBoxBaseApplication;
import com.squirrelbox.base.util.Keys;

public class SquirrelBoxRestClient {
	private static final String TAG = SquirrelBoxRestClient.class.getSimpleName();

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void setCookieStore(PersistentCookieStore myCookieStore) {
		client.setCookieStore(myCookieStore);
	}

	public static void setTimeout(int timeout) {
		client.setTimeout(timeout);
	}

	/*********************************************************************
	 * GET
	 *********************************************************************/
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		Log.i(TAG, "Request (GET): " + getAbsoluteUrl(url));
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void authGet(String url, Context context, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (params == null) {
			params = new RequestParams();
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");
		params.put("auth_token", token);

		Log.i(TAG, "Request (GET): " + getAbsoluteUrl(url));
		Log.i(TAG, "Params: " + params.toString());

		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	/*********************************************************************
	 * POST
	 *********************************************************************/
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		Log.i(TAG, "Request (POST): " + getAbsoluteUrl(url));
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, Context context, JSONObject jsonObject, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		try {
			Log.i(TAG, "Request (POST): " + getAbsoluteUrl(url));
			StringEntity entity = new StringEntity(jsonObject.toString());
			client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// Post using JSON Object
	public static void authPost(String url, Context context, JSONObject jsonObject, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");
		try {
			Log.i(TAG, "Request (POST): " + getAbsoluteUrl(url));
			jsonObject.put("auth_token", token);
			StringEntity entity = new StringEntity(jsonObject.toString());
			client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// POST using RequestParams
	public static void authPost(String url, Context context, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (params == null) {
			params = new RequestParams();
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");
		params.put("auth_token", token);

		Log.i(TAG, "Request (POST): " + getAbsoluteUrl(url));
		Log.i(TAG, "Params: " + params.toString());

		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/*********************************************************************
	 * PUT
	 *********************************************************************/
	public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		Log.i(TAG, "Request (PUT): " + getAbsoluteUrl(url));
		client.put(getAbsoluteUrl(url), params, responseHandler);
	}

	// PUT using JSON Object
	public static void authPut(String url, Context context, JSONObject jsonObject, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");
		try {
			Log.i(TAG, "Request (PUT): " + getAbsoluteUrl(url));
			jsonObject.put("auth_token", token);
			StringEntity entity = new StringEntity(jsonObject.toString());
			client.put(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// PUT using RequestParams
	public static void authPut(String url, Context context, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (params == null) {
			params = new RequestParams();
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");
		params.put("auth_token", token);

		Log.i(TAG, "Request (PUT): " + getAbsoluteUrl(url));
		Log.i(TAG, "Params: " + params.toString());

		client.put(getAbsoluteUrl(url), params, responseHandler);
	}

	/*********************************************************************
	 * DELETE
	 *********************************************************************/
	public static void delete(String url, List<NameValuePair> params, AsyncHttpResponseHandler responseHandler) {
		Log.i(TAG, "Request (DELETE): " + getAbsoluteUrl(url));
		client.delete(url, responseHandler);
	}

	public static void authDelete(String url, Context context, AsyncHttpResponseHandler responseHandler) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Keys.AUTH_TOKEN, "");

		Log.i(TAG, "Request (DELETE): " + getAbsoluteUrl(url));

		client.delete(getAbsoluteUrl(url) + "?auth_token=" + token, responseHandler);
	}

	/*********************************************************************
	 * GENERAL
	 *********************************************************************/
	private static String getAbsoluteUrl(String relativeUrl) {
		return SquirrelBoxBaseApplication.SERVER_URL + relativeUrl;
	}
}

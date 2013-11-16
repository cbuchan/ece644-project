package com.squirrelbox.base;

import android.app.Application;
import android.util.Log;

import com.squirrelbox.base.api.NetworkProvider;

public class SquirrelBoxBaseApplication extends Application {

	private static final String TAG = SquirrelBoxBaseApplication.class.getSimpleName();

	public static String SERVER_URL = "http://squirrelauth.herokuapp.com";
	public static boolean IS_LOCAL;

	// Providers
	protected NetworkProvider networkProvider;

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Creating SquirrelBoxBaseApplication");

		networkProvider = new NetworkProvider(this);
	}

	public NetworkProvider getNetworkProvider() {
		return networkProvider;
	}
}

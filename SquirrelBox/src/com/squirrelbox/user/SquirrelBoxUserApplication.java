package com.squirrelbox.user;

import android.util.Log;

import com.squirrelbox.base.SquirrelBoxBaseApplication;

public class SquirrelBoxUserApplication extends SquirrelBoxBaseApplication {

	private static final String TAG = SquirrelBoxUserApplication.class.getSimpleName();

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Creating SquirrelBoxUserApplication");
	}
}

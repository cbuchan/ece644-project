package com.squirrelbox.base.api;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.squirrelbox.base.data.model.User;

public class JSONBuilder {

	public JSONObject createUserJSON(User user, String password) throws JSONException, UnsupportedEncodingException {
		JSONObject userRequestJson = new JSONObject(); // top level

		JSONObject userJson = new JSONObject();
		userJson.put("first_name", user.getFirstName());
		userJson.put("last_name", user.getLastName());
		userJson.put("email", user.getEmail());
		userJson.put("password", password);
		userJson.put("registration_source", "android");

		userRequestJson.put("user", userJson);
		return userRequestJson;
	}

	public JSONObject createUserJSON(User user) throws JSONException, UnsupportedEncodingException {
		JSONObject userRequestJson = new JSONObject(); // top level

		JSONObject userJson = new JSONObject();
		userJson.put("first_name", user.getFirstName());
		userJson.put("last_name", user.getLastName());
		userJson.put("email", user.getEmail());

		userRequestJson.put("user", userJson);
		return userRequestJson;
	}

}

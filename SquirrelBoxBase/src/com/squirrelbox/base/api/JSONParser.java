package com.squirrelbox.base.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squirrelbox.base.data.model.Box;
import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.base.data.model.User;

public class JSONParser {

	private static final String TAG = JSONParser.class.getSimpleName();

	public static Token parseToken(JSONObject tokenResponseObject) throws JSONException {
		Token token = new Token();

		if (tokenResponseObject.has("auth_token")) {
			token.setAuthToken(tokenResponseObject.optString("auth_token"));
		}

		return token;
	}

	public static ArrayList<Box> parseBoxes(JSONObject boxJsonWrapper) throws JSONException {
		ArrayList<Box> boxes = new ArrayList<Box>();

		JSONArray boxesJson = boxJsonWrapper.optJSONArray("boxes");

		if (boxesJson != null) {
			for (int i = 0; i < boxesJson.length(); i++) {
				Box box = parseBox(boxesJson.optJSONObject(i));
				boxes.add(box);
			}
		}

		return boxes;
	}

	public static Box parseBox(JSONObject boxJson) {
		Box box = new Box();

		if (boxJson != null) {
			box.setStatus(boxJson.optString("status"));
			box.setPermission(boxJson.optString("permission"));
			box.setId(boxJson.optInt("boxid"));
			box.setKeyHolder(parseUser(boxJson.optJSONObject("user")));
		}

		return box;
	}

	public static ArrayList<User> parseUsers(JSONObject userJsonWrapper) throws JSONException {
		ArrayList<User> users = new ArrayList<User>();

		JSONArray usersJson = userJsonWrapper.optJSONArray("users");

		if (usersJson != null) {
			for (int i = 0; i < usersJson.length(); i++) {
				User user = parseUser(usersJson.optJSONObject(i));
				users.add(user);
			}
		}

		return users;
	}

	public static User parseUser(JSONObject userJson) {
		User user = new User();

		if (userJson != null) {
			user.setUserId(userJson.optInt("uid"));
			user.setUsername(userJson.optString("username"));
		}

		return user;
	}

	public static String parseErrors(JSONObject jsonObject) throws JSONException {
		if (jsonObject.has("errors") && jsonObject.getJSONObject("errors").length() > 0) {
			JSONObject errors = jsonObject.getJSONObject("errors");

			StringBuilder errorMessage = new StringBuilder("Error: ");
			JSONArray names = errors.names();

			if (names != null) {
				for (int i = 0; i < names.length(); i++) {
					String title = names.getString(i);
					errorMessage.append(title);
					int titleIndex = errorMessage.indexOf(title);
					errorMessage.setCharAt(titleIndex, Character.toUpperCase(errorMessage.charAt(titleIndex)));
					errorMessage.append(" ");

					JSONArray errorList = errors.getJSONArray(title);

					for (int j = 0; j < errorList.length(); j++) {
						errorMessage.append(errorList.getString(j));
						if (j != errorList.length() - 1) {
							errorMessage.append(", ");
						} else {
							errorMessage.append(". ");
						}
					}
				}
			}

			return errorMessage.toString();
		} else {
			return null;
		}
	}
}

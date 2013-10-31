package com.squirrelbox.base.data;

import com.squirrelbox.base.data.model.Token;
import com.squirrelbox.base.data.model.User;


public class DataCallbackHandler {

	public void onSuccess(String successMessage) {
	};

	public void onFailure(String errorMessage) {
	};

	public void onTokenSuccess(Token token) {
	};

	public void onUserSuccess(User user) {
	};
}

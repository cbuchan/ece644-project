package com.squirrelbox.base.data;

import java.util.ArrayList;

import com.squirrelbox.base.data.model.Box;
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

	public void onBoxStatusSuccess(ArrayList<Box> boxes) {
	};

	public void onReservationSuccess() {
	};

	public void onRelinquishSuccess() {
	};
}

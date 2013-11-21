package com.squirrelbox.base.data.model;

import com.squirrelbox.base.util.Keys;

public class User {
	private int userId = Keys.NONE;
	private String username;

	public User() {

	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}

package com.squirrelbox.base.data.model;

public class Box {
	int id;
	String status;
	String permission;
	User keyHolder;

	public User getKeyHolder() {
		return keyHolder;
	}

	public void setKeyHolder(User keyHolder) {
		this.keyHolder = keyHolder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

}

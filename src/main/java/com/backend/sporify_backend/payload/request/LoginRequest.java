package com.backend.sporify_backend.payload.request;

import javax.validation.constraints.NotBlank;

/**
 * Τα στοιχεία που στέλνει ο χρήστης όταν στέλνει ένα login request,
 * δηλαδή username & password
 */

public class LoginRequest {
	private String username;

	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

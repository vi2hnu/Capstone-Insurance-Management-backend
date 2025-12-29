package org.example.identityservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
	private String id;
	private String username;
	private String email;
	private List<String> roles;
    private boolean changePassword;

	public UserInfoResponse(String id, String username, String email, List<String> roles, boolean changePassword) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
        this.changePassword = changePassword;
	}
}

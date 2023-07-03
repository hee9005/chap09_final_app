package org.edupoll.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestData {
	
	@NotBlank
	String email;
	@NotBlank
	String password;


	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public LoginRequestData() {
		super();
	}
	

	
	
}



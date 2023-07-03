package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateUserResponse {
	
	private int code;
	private String token;
	private String userEmail;
}

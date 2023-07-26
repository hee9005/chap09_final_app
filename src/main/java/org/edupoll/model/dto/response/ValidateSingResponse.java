package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateSingResponse {
	private int code;
	private String type;
	private String token;
	private String userEmail;
}

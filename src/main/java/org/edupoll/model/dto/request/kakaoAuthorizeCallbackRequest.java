package org.edupoll.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class kakaoAuthorizeCallbackRequest {

	private String code;
	private String error;
}

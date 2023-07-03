package org.edupoll.model.dto.request;

import java.util.Date;

import org.edupoll.model.entity.VerificationCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CertifiedRequest {
	@Email
	private String email; //인증코드를 발급시킨 이메일
	@NotBlank
	private String code; // 인증 코드

}

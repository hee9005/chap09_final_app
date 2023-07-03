package org.edupoll.model.dto.response;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.edupoll.model.entity.VerificationCode;

import lombok.Data;

@Data
public class VerificationCodeResponseDate {
	

private String email; //인증코드를 발급시킨 이메일
	
	private String code; // 인증 코드
	
	private String created; // 발급된 날짜
	
	private String state; // 인증 상태

	public VerificationCodeResponseDate(VerificationCode saved) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.code =saved.getCode();
		this.email = saved.getEmail();
		this.created= sdf.format(saved.getCreated());
		this.state = saved.getState();
	}
	
}

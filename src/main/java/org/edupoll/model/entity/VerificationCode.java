package org.edupoll.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "verificationCodes")
public class VerificationCode {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String email; //인증코드를 발급시킨 이메일
	
	private String code; // 인증 코드
	
	private Date created; // 발급된 날짜
	
	private String state; // 인증 상태
	
	private Date expired;
	
	/**사용자의 이메일과 보안코드 인증상태를 db에 저장하는 사용자*/
	public VerificationCode(String code, String email, String state) {
		super();
		this.code =code;
		this.email = email;
		this.state = state;
	}
	
	@PrePersist
	public void prePersist() {
		created = new Date();
	}

	public VerificationCode() {
		super();
	}
	
	
}

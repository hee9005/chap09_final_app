package org.edupoll.controller;

import org.edupoll.exception.AlreadyVerifedException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPassword;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistCodeException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.NotSubscribedEmail;
import org.edupoll.exception.SubscribedEmail;
import org.edupoll.exception.verifyCodeException;
import org.edupoll.model.dto.request.CertifiedRequest;
import org.edupoll.model.dto.request.LoginRequestData;
import org.edupoll.model.dto.request.MailTestRequest;
import org.edupoll.model.dto.request.UserCreateRequestData;
import org.edupoll.model.dto.request.UserPasswordModify;
import org.edupoll.model.dto.request.userPasswordRequest;
import org.edupoll.model.dto.response.UserResponseDate;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.model.dto.response.VerifyEmailResponse;
import org.edupoll.service.JwtService;
import org.edupoll.service.MailService;
import org.edupoll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin("http://192.168.4.107:3000")
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	JwtService jwtService;
	
	 
	@DeleteMapping
	public ResponseEntity<Void> deleteUserHandle(@AuthenticationPrincipal String principal,userPasswordRequest req) throws InvalidPassword, InvalidPasswordException, NotExistUserException {
		String emailTokenValue = jwtService.verifyToken(principal);
		userService.deleteSpecificUser(emailTokenValue,req);
	
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@PatchMapping("/private/password")
	public ResponseEntity<Void> passwordModifyHandle(@Valid UserPasswordModify dto
			,@RequestHeader(name="token") String token) throws InvalidPassword{
		if(token == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		userService.passwordModif(dto,token);
		return null;
	}
	// 회원가입 API완료
	@PostMapping("/join")
	public ResponseEntity<UserResponseDate> userJoinHandle(@Valid UserCreateRequestData dto) throws ExistUserEmailException, verifyCodeException{
	userService.registerNewUser(dto);

	return new ResponseEntity<>(HttpStatus.CREATED);
		
	}
	// 이메일 사용가능한지 아닌지 확인해주는 API 완료
	@GetMapping("/available")
	public ResponseEntity<Void> avaiableHandle(@Valid MailTestRequest email)
			throws ExistUserEmailException {
		userService.emailAvailableCheck(email);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	// 토큰 발급해주는 API
	@PostMapping("/login")
	public ResponseEntity<ValidateUserResponse> loginUserHandel(@Valid LoginRequestData dto) throws NotSubscribedEmail, InvalidPassword{
		
		userService.validateUser(dto);
		
		String token = jwtService.createToken(dto.getEmail());
		
		log.info("token = " + token);
		
		var response = new ValidateUserResponse(200, token, dto.getEmail());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
//	@PostMapping("/mail-test")
//	public ResponseEntity<Void> mailTestHandle(MailTestRequest req){
//		mailService.sendTesteSimpleMail(req);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
	
	//이메일 인증코드 발급API 완료
	@PostMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> sendTesteHtmlMail(MailTestRequest req) throws MessagingException, AlreadyVerifedException{
		mailService.sendTesteHtmlMail(req);
		VerifyEmailResponse response = new VerifyEmailResponse(200,"인증코드가 발송되었습니다 이메일을 확인하세요");
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	// 이메일 인증코드 검증하는 API완료
	@PatchMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> emailCertified (CertifiedRequest req) throws verifyCodeException, NotExistCodeException {
		mailService.emailCertified(req);
		VerifyEmailResponse response = new VerifyEmailResponse(200,"인증이 완료되었습니다.");
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}

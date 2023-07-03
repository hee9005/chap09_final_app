package org.edupoll.controller;

import java.io.IOException;

import org.edupoll.exception.InvalidPassword;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.request.userPasswordRequest;
import org.edupoll.model.dto.response.LogonUserInfoResponse;
import org.edupoll.model.dto.response.upResponse;
import org.edupoll.service.JwtService;
import org.edupoll.service.UserService;
import org.edupoll.service.kakaoAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.NotSupportedException;

@RequestMapping("/api/v1/user/private")
@CrossOrigin
@RestController
public class PrivateController {
	@Autowired
	UserService userService;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	kakaoAPIService kakaoAPIService;
	
	@GetMapping
	public ResponseEntity<?> getLogonUserHandle(Authentication authentication ) throws NotExistUserException{
		String email = (String) authentication.getPrincipal();
		UserWrapper wrapper = userService.searchUserByEmail(email);
		LogonUserInfoResponse response = new LogonUserInfoResponse(200, wrapper);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> deleteUserHandle(@AuthenticationPrincipal String principal,userPasswordRequest req) throws InvalidPassword, InvalidPasswordException, NotExistUserException {
		String emailTokenValue = jwtService.verifyToken(principal);
		if( emailTokenValue.endsWith("@kakao.user")) { // 소셜로 가입한 유저 삭제하기
			// 카카오에서 unlink 요청하기
			kakaoAPIService.sendUnlink(emailTokenValue);
			// DB에서 데이터 삭제하기
		}else {
			userService.deleteSpecificUser(emailTokenValue,req);			
		}
	
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	// 사용자 상태 업데이트(프로필 이미지 / 이름) 처리할 API
	// 파일업로드는 컨테츠타입이 multipart/form-data 로 들어옴
	// (file과 text 유형이 섞여있음)
	@PostMapping("/info")
	public ResponseEntity<?> updateProfileHandle(@AuthenticationPrincipal String principal,
			UpdateProfileRequest request) throws IOException, NotSupportedException, NotExistUserException {
		
		userService.modifySpecificUser(principal,request);
		UserWrapper wrapper = userService.searchUserByEmail(principal);
		var response = new LogonUserInfoResponse(200, wrapper);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}

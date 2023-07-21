package org.edupoll.controller;

import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.kakaoAccessTokenWeapper;
import org.edupoll.model.dto.request.ValidateKakaoRequest;
import org.edupoll.model.dto.request.kakaoAuthorizeCallbackRequest;
import org.edupoll.model.dto.response.OAuthSignResponse;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.service.JwtService;
import org.edupoll.service.UserService;
import org.edupoll.service.kakaoAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/oauth")
public class OAuthController {
	
	@Value("${kakao.restapi.key}")
	String kakaoRestApiKey;
	@Value("${kakao.redirect.url}")
	String kakaoRedirectUrl;
	
	@Autowired
	kakaoAPIService kakaoAPIService;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserService userService;
	// 카카오 인증 요청시 인증해야될 주소 알려주는 API 완료
	@GetMapping("/kakao")
	public ResponseEntity<OAuthSignResponse> oauthKakaoHandle() {
		
		var response = new OAuthSignResponse(200,
				"https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoRestApiKey
						+ "&redirect_uri=" + kakaoRedirectUrl);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

		// 카카오 인증코드로 사용자 정보얻어내는 API 완료
	@PostMapping("/kakao")
	public ResponseEntity<ValidateUserResponse> oauthKakaoPostHandle(ValidateKakaoRequest req) 
				throws JsonMappingException, JsonProcessingException {
		kakaoAccessTokenWeapper wrapper =kakaoAPIService.getAccessToken(req.getCode());
		KakaoAccount account =kakaoAPIService.getUserInfo(wrapper.getAccessToken());
		userService.updeteKakaoUser(account, wrapper.getAccessToken());
		
		log.info("kakao = {}", account.toString() );
		String token = jwtService.createToken(account.getEmail());
		ValidateUserResponse response = new ValidateUserResponse(200, token, account.getEmail());
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
}

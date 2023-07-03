package org.edupoll.config;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPassword;
import org.edupoll.exception.NotSubscribedEmail;
import org.edupoll.exception.SubscribedEmail;
import org.edupoll.exception.verifyCodeException;
import org.edupoll.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;

@ControllerAdvice
public class ExceptionHandlerConfiguration {

	@ExceptionHandler(SubscribedEmail.class)
	public ResponseEntity<ErrorResponse> SubscribedEmailHabdle(SubscribedEmail e){
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(InvalidPassword.class)
	public ResponseEntity<ErrorResponse> SubscribedEmailHabdle(InvalidPassword e){
		
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(NotSubscribedEmail.class)
	public ResponseEntity<ErrorResponse> SubscribedEmailHabdle(NotSubscribedEmail e){
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler({JWTDecodeException.class,TokenExpiredException.class})
	public ResponseEntity<ErrorResponse> jwtException(Exception ex){
		var response = new ErrorResponse(401,"token value is expired or damaged",System.currentTimeMillis());
		return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(verifyCodeException.class)
	public ResponseEntity<ErrorResponse> verifyCodeExceptionHabdle(verifyCodeException e){
		ErrorResponse response = new ErrorResponse(400, e.getMessage(), System.currentTimeMillis());
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(ExistUserEmailException.class)
	public ResponseEntity<Void> exceptionHandle(ExistUserEmailException ex) {

		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
}

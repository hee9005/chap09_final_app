package org.edupoll.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	
	@Value("${jwt.secret.key}")
	String secretKey;
	@Transactional
	public String createToken(String email) {
		
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		
		return JWT.create()
		.withIssuer("finalApp")
		.withClaim("email", email)
		.withIssuedAt(new Date(System.currentTimeMillis()))
		.withExpiresAt(new Date(System.currentTimeMillis()+1000*60*30))
		.sign(algorithm);
		
	}
	@Transactional
	public String verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		var verifier =JWT.require(algorithm).build();
		DecodedJWT decodedJWT =  verifier.verify(token);
		log.info(decodedJWT.getIssuer());
		log.info(decodedJWT.getClaim("email").asString());
		return decodedJWT.getClaim("email").asString();
	}
}

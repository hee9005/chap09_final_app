package org.edupoll.config.support;

import java.io.IOException;
import java.util.ArrayList;

import org.edupoll.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	JwtService jwtService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 사용자가 JWT 토큰을 안 가지고 왔다면 
		String authorization = request.getHeader("Authorization");
		log.info("Authorization header value : {}", authorization);
	
		if(authorization == null) {
			log.info("Did not process authentication request since failed to find authorization header", authorization);
			filterChain.doFilter(request, response);	// 통과 시켜주면 됨.
			return;
		}
		
		try {
			// JWT 유효성 검사 해서 통과하면
			String email = jwtService.verifyToken(authorization);	//
			// 여기까지 왔으면 통과를 한거임.
			Authentication authentication = 
					new UsernamePasswordAuthenticationToken(email, authorization, new ArrayList<>());
						// principal ===> 인증주체자 : UserDetails 객체가 보통 설정됨. 
							// 	@AuthenticationPrincipal 했을때 나오는 값
						// credentials ==> 인증에 사용됬던 정보 (크게 상관없음)
			
						// authorities  ===> 권한 : role 에 따른 차단
			
			// 인증통과 상태로 만들어 버리자.
			// log.info("{}", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}catch(Exception e) {
			// 토큰이 만료됬거나 위조됬거나 한 상황
			log.error("Verify token fail. {}", e.getMessage());
			throw new BadCredentialsException("Invalid authentication token");
		}
				
		
		
		filterChain.doFilter(request, response);
	}
	

}

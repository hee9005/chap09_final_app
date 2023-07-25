package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Optional;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPassword;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.NotSubscribedEmail;
import org.edupoll.exception.verifyCodeException;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.LoginRequestData;
import org.edupoll.model.dto.request.MailTestRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.request.UserCreateRequestData;
import org.edupoll.model.dto.request.UserPasswordModify;
import org.edupoll.model.dto.request.userPasswordRequest;
import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.edupoll.model.entity.ProfileImage;
import org.edupoll.model.entity.User;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.FeedAttachRepository;
import org.edupoll.repository.FeedRepository;
import org.edupoll.repository.ProfileImageRepository;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationCodeRepository verificationCodeRepository;

	@Value("${jwt.secret.key}")
	String secretKey;

	@Value("${upload.basedir}")
	String baseDir;
	@Value("${upload.server}")
	String uploadServer;

	@Autowired
	ProfileImageRepository profileImageRepository;
	
	@Autowired
	FeedRepository feedRepository;

	@Autowired
	FeedAttachRepository feedAttachRepository;
	
	@Transactional
	public void deleteSpecificSocialUser(String userEmail) throws NotExistUserException {
		var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotExistUserException());

		userRepository.delete(user);
	}

	@Transactional
	public void registerNewUser(UserCreateRequestData dto) throws ExistUserEmailException, verifyCodeException {
//		User found = userRepository.findByEmail(dto.getEmail());
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new ExistUserEmailException();
		}
		// 인증절차를 거쳤는지 확인
		VerificationCode found = verificationCodeRepository.findTop1ByEmailOrderByCreatedDesc(dto.getEmail())
				.orElseThrow(() -> new verifyCodeException("인증코드 검증 기록이 존재하지않습니다.."));
		if (found.getState() == null) {
			throw new verifyCodeException("아직 미 인증 상태입니다.");
		}

		// 회원정보 등록
		User one = new User();
		one.setEmail(dto.getEmail());
		one.setName(dto.getName());
		one.setPassword(dto.getPassword());
		userRepository.save(one);

	}

	@Transactional
	public void validateUser(LoginRequestData req) throws NotSubscribedEmail, InvalidPassword {
		var found = userRepository.findByEmail(req.getEmail());
		if (found == null) {
			throw new NotSubscribedEmail();
		}

		boolean isSame = found.get().getPassword().equals(req.getPassword());
		if (!isSame) {
			throw new InvalidPassword();
		}
		// .............
	}

	/** 비밀번호 변경 */
	@Transactional
	public void passwordModif(UserPasswordModify dto, String token) throws InvalidPassword {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		var verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(token);
		String email = decodedJWT.getClaim("email").asString();
		var user = userRepository.findOneByEmail(email);
		if (!user.getPassword().equals(dto.getFormerPass())) {
			throw new InvalidPassword();
		}
		user.setPassword(dto.getRenewedPass());
		userRepository.save(user);
	}

	@Transactional
	public void deleteSpecificUser(String userEmail, userPasswordRequest req)
			throws NotExistUserException, InvalidPasswordException {
		
		var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotExistUserException());
		var feeds = feedRepository.findByWiter(user);
		if (!user.getPassword().equals(req.getPassword())) {

			throw new InvalidPasswordException();
		}
		for(Feed fees : feeds) {
			feedAttachRepository.deleteAllByfeedId(fees.getId());
		}
		feedRepository.deleteAllByWiter(user);
		userRepository.delete(user);
		verificationCodeRepository.deleteByEmail(userEmail);
	}

	@Transactional
	public void emailAvailableCheck(@Valid MailTestRequest email) throws ExistUserEmailException {
		boolean rst = userRepository.existsByEmail(email.getEmail());
		if (rst) {
			throw new ExistUserEmailException();
		}

	}

	public void updeteKakaoUser(KakaoAccount account, String accessToken) {
		Optional<User> _user = userRepository.findByEmail(account.getEmail());
		if (_user.isPresent()) {
			User saved = _user.get();
			saved.setSocial(accessToken);
			userRepository.save(saved);
		} else {
			User user = new User();
			user.setEmail(account.getEmail());
			user.setName(account.getNickname());
			user.setProfileImage(account.getProfileImage());
			user.setSocial(accessToken);
			userRepository.save(user);
		}
	}

	@Transactional
	// 특정유저 정보 업데이트
	public void modifySpecificUser(String userEmail, UpdateProfileRequest request)
			throws IOException, NotSupportedException {

//		log.info("req.name = {}", request.getName());
//		log.info("req.profile = {} / {}", request.getProfile().getContentType(),
//				request.getProfile().getOriginalFilename());
		// 리퀘스트 객체에서 파일 정보를 뽑자
		var foundUser = userRepository.findByEmail(userEmail).get(); // 있는지 없는지 체크
		foundUser.setName(request.getName());
		if(request.getProfile() != null) {
		MultipartFile multi = request.getProfile();
		// 해당 파일이 컨텐츠 타입이 이미지인 경우에만 처리
		if (!multi.getContentType().startsWith("image/")) {
			throw new NotSupportedException("이미지 파일만 설정가능합니다.");
		}
			
		
		// 파일을 옮기는 작업
		// 기본 세이브경로는 propertis에서
		String emailEncoded = new String(Base64.getEncoder().encode(userEmail.getBytes()));
		
		File saveDir = new File(baseDir+"/profile/"+emailEncoded);
		saveDir.mkdirs();
		
		// 파일명은 로그인사용자의 이메일주소를 활용해서
		String filename = System.currentTimeMillis()
				+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));

		File dest = new File(saveDir, filename);

		// 두개 조합해서 옮길 장소 설정
		// 옮겨두기
		multi.transferTo(dest); // 업로드 됨.
		// 파일 정보를 DB에 insert
		foundUser.setProfileImage(uploadServer+"/resource/profile/"+emailEncoded+"/"+filename);
		userRepository.save(foundUser);
		}
		userRepository.save(foundUser);
		
	}
	public Resource loadResource(String url) throws NotExistUserException, MalformedURLException {

		log.warn("resource = {}", url);
		var found = profileImageRepository.findTop1ByUrl(url).orElseThrow(() -> new NotExistUserException());
		return new FileUrlResource(found.getFileAddress());

	}

	public UserWrapper searchUserByEmail(String emailTokenValue) throws NotExistUserException {
		var found = userRepository.findByEmail(emailTokenValue).orElseThrow(() -> new NotExistUserException());
		return new UserWrapper(found);
	}
}

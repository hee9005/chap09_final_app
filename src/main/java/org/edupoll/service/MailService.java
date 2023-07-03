package org.edupoll.service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.edupoll.exception.AlreadyVerifedException;
import org.edupoll.exception.NotExistCodeException;
import org.edupoll.exception.verifyCodeException;
import org.edupoll.model.dto.request.CertifiedRequest;
import org.edupoll.model.dto.request.MailTestRequest;
import org.edupoll.model.dto.response.VerificationCodeResponseDate;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	
	private static final Date TIMESTAMP = null;

	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	VerificationCodeRepository verificationCodeRepository;
	
	public void sendTesteSimpleMail(MailTestRequest req) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("hee900590@gmail.com");
		message.setTo(req.getEmail());
		message.setSubject("메일테스트");
		message.setText("메일 테스트 중입니다.\n불편을 드려 죄송합니다.");
		
		javaMailSender.send(message);
		
	}
	
	/**사용자에게 인증코드를 보내고 db에 저장하는 서비스
	 * @throws AlreadyVerifedException */
	public VerificationCodeResponseDate sendTesteHtmlMail(MailTestRequest req) throws MessagingException, AlreadyVerifedException {
	
		Optional<VerificationCode> found = verificationCodeRepository.findTop1ByEmailOrderByCreatedDesc(req.getEmail());
		if(found.isPresent()&& !found.get().getState().equals("N")){
			throw new AlreadyVerifedException();
		}
		Random random = new Random();
		int randNum =random.nextInt(1_000_000);
		String code = String.format("%06d", randNum);
		MimeMessage message=javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(req.getEmail());
		helper.setFrom("hee900590@gmail.com");
		helper.setSubject("메일테스트-2");
		helper.setText(""" 
				<div>
				<h1>인증코드</h1>
				<p style="color:orange">
					이메일 본인 인증 절차에 따라 인증코드를 보내드립니다.
					<p>
						쿠폰번호 : <i>#code</i>
					</p>
				</div>
				""".replaceAll("#code", code),true);
				
		javaMailSender.send(message);
	
		VerificationCode issuance = new VerificationCode(code, req.getEmail(), "N");
		
		VerificationCode saved = verificationCodeRepository.save(issuance);
		
		return new VerificationCodeResponseDate(saved);
	}
	
	
	/**사용자에게 보낸 이증코드를 확인하는 서비스
	 * @throws NotExistCodeException */
	public void emailCertified(CertifiedRequest req) throws verifyCodeException, NotExistCodeException {
		System.out.println(req.getEmail());	
		System.out.println("===============");
		System.out.println(req.getCode());
		
		Optional<VerificationCode> result = verificationCodeRepository
				.findTop1ByEmailOrderByCreatedDesc(req.getEmail());

		VerificationCode found = result.orElseThrow(() -> new NotExistCodeException());
		
		long elapsed = System.currentTimeMillis() - found.getCreated().getTime();
		if (elapsed > 1000 * 60 * 10) {
			throw new verifyCodeException("인증코드 유효시간이 만료되었습니다.");
		}
		if (!found.getCode().equals(req.getCode())) {
			throw new verifyCodeException("인증코드가 일치하지 않습니다.");
		}
		found.setState("Y");
		verificationCodeRepository.save(found);
		
	}
}

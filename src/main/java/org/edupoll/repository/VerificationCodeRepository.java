package org.edupoll.repository;

import java.util.Optional;

import org.edupoll.model.dto.request.CertifiedRequest;
import org.edupoll.model.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{
	public Optional<VerificationCode> findTop1ByEmailOrderByCreatedDesc(String email);

	public void deleteByEmail(String email);

}

package org.edupoll.repository;

import java.util.Optional;

import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long>{
	public Optional<User> findByEmail(String email);
	 User findOneByEmail(String email);
	 
	 public boolean existsByEmail(String email);
	public UserWrapper findOneById(Long id);

}

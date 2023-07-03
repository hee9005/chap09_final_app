package org.edupoll.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "profileImages")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImage {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private String fileAddress; // 실제파일 위치
	private String url; // 웹상 위치

}

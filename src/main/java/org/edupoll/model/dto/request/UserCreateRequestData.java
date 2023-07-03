package org.edupoll.model.dto.request;

import org.edupoll.model.entity.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateRequestData {
	@NotNull
	String email;
	@NotNull
	@NotNull
	String name;
	String password;

	
	
}

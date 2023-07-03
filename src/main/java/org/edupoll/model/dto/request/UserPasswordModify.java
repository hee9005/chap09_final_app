package org.edupoll.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPasswordModify {
	/**이전 비번*/
	@NotNull
	String FormerPass;
	/**새로운 비번*/
	@NotNull
	String RenewedPass;
}

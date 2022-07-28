package idv.blake.application.model.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestEntity {

	@Schema(description = "帳號", required = true)
	private String account;

	@Schema(description = "密碼(須使用SHA256加密過後全小寫)", required = true)
	private String password;

}

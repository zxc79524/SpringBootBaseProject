package idv.blake.application.model.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseEntity {

	@Schema(description = "accessToken", required = true)
	private String accessToken;

	@Schema(description = "refreshToken", required = true)
	private String refreshToken;

	@Schema(description = "使用者ID", required = true)
	private String uid;

	@Schema(description = "使用者帳號", required = true)
	private String account;
}

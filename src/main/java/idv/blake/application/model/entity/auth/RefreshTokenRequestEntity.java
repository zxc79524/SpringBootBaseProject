package idv.blake.application.model.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestEntity {

	@Schema(description = "refresh token", required = true)
	private String refreshToken;
}

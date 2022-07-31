package idv.blake.application.model.entity.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRecord {

	private String permissionId;
	private String httpMethod;
	private String apiUrl;
	private boolean isAllow;

}

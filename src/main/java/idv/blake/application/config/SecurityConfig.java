package idv.blake.application.config;

public class SecurityConfig {

	public static final String ACCESS_SECRET = "ACCESS_SECRET";
	public static final long ACCESS_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000;
	public static final String ACCESS_PREFIX = "Bearer ";
	public static final String ACCESS_HEADER = "Authorization";

	public static final String REFRESH_SECRET = "REFRESH_SECRET";
	public static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
	public static final String REFRESH_PREFIX = "refresh_";

	public static final String AUTH_URL = "/auth";

}

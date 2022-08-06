package idv.blake.application.config;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;
import idv.blake.application.model.entity.auth.TokenDbEntity;
import idv.blake.application.model.entity.security.AuthRecordData;
import idv.blake.application.model.entity.security.AuthRecordListData;
import idv.blake.application.model.exception.UnauthorizedException;
import idv.blake.application.model.service.auth.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LoginAuthorizationFilter extends BasicAuthenticationFilter {

	private AccountDao accountDao;
	private RolePermissionDao rolePermissionDao;
	private TokenDao tokenDao;

	private StringRedisTemplate stringRedisTemplate;

	private AuthService authService;

	public LoginAuthorizationFilter(AuthenticationManager authenticationManager, AuthService authService,
			TokenDao tokenDao) {
		super(authenticationManager);
		this.authService = authService;
		this.tokenDao = tokenDao;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String accessToken = request.getHeader(SecurityConfig.ACCESS_HEADER);

		if (accessToken == null || !accessToken.startsWith(SecurityConfig.ACCESS_PREFIX)) {
			response.sendError(401, "Cannot find access Token");
			return;
		}

		try {
			String uidString = getUidFromAccessToken(accessToken.replace(SecurityConfig.ACCESS_PREFIX, ""));
			request.setAttribute("uid", uidString);

			AuthRecordListData authRecords = authService.getPermission(uidString);
			String servletPath = request.getServletPath().toLowerCase();

			for (AuthRecordData record : authRecords.getAuthRecordDatas()) {
				Pattern FILTERS = Pattern.compile(record.getApiUrl());
				if (FILTERS.matcher(servletPath).matches() && request.getMethod().equals(record.getHttpMethod())) {
					if (record.isAllow()) {

						chain.doFilter(request, response);
						return;
					}
				}
			}

			response.sendError(403, "User permission denied");
			return;

		} catch (UnauthorizedException e) {
			e.printStackTrace();
			response.sendError(401, e.getMessage());
			return;
		}

//		chain.doFilter(request, response);
	}

	private String getUidFromAccessToken(String accessToken) throws UnauthorizedException {
		Claims result;
		try {
			result = Jwts.parser().setSigningKey(SecurityConfig.ACCESS_SECRET.getBytes()).parseClaimsJws(accessToken)
					.getBody();

		} catch (Exception e) {
			throw new UnauthorizedException("Token fail");
		}

		TokenDbEntity tokenDbEntity = tokenDao.findByTokenId(result.get("tokenId", String.class));
		if (tokenDbEntity == null) {
			throw new UnauthorizedException("Token invalid");
		}

		if (tokenDbEntity.getExpireDate().getTime() < System.currentTimeMillis()) {
			throw new UnauthorizedException("Token expired");
		}

		return tokenDbEntity.getAccount().getUid();
	}

}

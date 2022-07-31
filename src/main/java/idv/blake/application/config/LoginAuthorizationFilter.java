package idv.blake.application.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;
import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.auth.TokenDbEntity;
import idv.blake.application.model.entity.permission.PermissionDbEntity;
import idv.blake.application.model.entity.permission.RolePermissionDbEntity;
import idv.blake.application.model.entity.security.AuthRecord;
import idv.blake.application.model.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LoginAuthorizationFilter extends BasicAuthenticationFilter {

	private AccountDao accountDao;
	private RolePermissionDao rolePermissionDao;
	private TokenDao tokenDao;

	public LoginAuthorizationFilter(AuthenticationManager authenticationManager, AccountDao accountDao,
			TokenDao tokenDao, RolePermissionDao rolePermissionDao) {
		super(authenticationManager);
		this.accountDao = accountDao;
		this.tokenDao = tokenDao;
		this.rolePermissionDao = rolePermissionDao;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

//		System.out.println(String.format("%s + %s", request.getRequestURI(), request.getMethod()));

		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
//		System.out.println("=====LoginAuthorizationFilter======");

		String accessToken = request.getHeader(SecurityConfig.ACCESS_HEADER);

		if (accessToken == null || !accessToken.startsWith(SecurityConfig.ACCESS_PREFIX)) {
			response.sendError(401, "Cannot find access Token");
			return;
		}

		try {
			String uidString = getUidFromAccessToken(accessToken.replace(SecurityConfig.ACCESS_PREFIX, ""));
			request.setAttribute("uid", uidString);

			List<AuthRecord> authRecords = getAuthFormUid(uidString);
			String servletPath = request.getServletPath().toLowerCase();

//			System.out.println("servletPath :" + servletPath);
//			System.out.println("request.getMethod() :" + request.getMethod());
//			System.out.println(new Gson().toJson(authRecords));

			for (AuthRecord record : authRecords) {
				Pattern FILTERS = Pattern.compile(record.getApiUrl());
				if (FILTERS.matcher(servletPath).matches() && request.getMethod().equals(record.getHttpMethod())) {
//					System.out.println(new Gson().toJson(record));
					if (record.isAllow()) {

						chain.doFilter(request, response);
						return;
					}
				}
			}

//			System.out.println(new Gson().toJson(authEntities));

			response.sendError(403, "User permission denied");
			return;

		} catch (UnauthorizedException e) {
			e.printStackTrace();
			response.sendError(401, e.getMessage());
			return;
		}

//		chain.doFilter(request, response);
	}

	@Transactional
	private List<AuthRecord> getAuthFormUid(String uid) throws UnauthorizedException {
		List<AuthRecord> authRecords = new ArrayList<>();

		AccountDbEntity accountDbEntity = accountDao.findByUid(uid);
		if (accountDbEntity == null) {
			throw new UnauthorizedException(String.format("Not Found Account by uid [%s]", uid));
		}

		if (accountDbEntity.getAccountRole() == null) {
			throw new UnauthorizedException(String.format("uid [%s] not hava role", uid));
		}

		List<RolePermissionDbEntity> rolePermissionDbEntities = rolePermissionDao
				.findByRole(accountDbEntity.getAccountRole().getRole());

		if (rolePermissionDbEntities == null) {
			throw new UnauthorizedException(String.format("uid [%s] role [%s] not have permission", uid,
					accountDbEntity.getAccountRole().getRole().getRoleId()));
		}

		for (RolePermissionDbEntity rolePermissionDbEntity : rolePermissionDbEntities) {

			PermissionDbEntity permissionDbEntity = rolePermissionDbEntity.getPermission();

			AuthRecord authEntity = AuthRecord.builder().apiUrl(permissionDbEntity.getApiUrl())
					.httpMethod(permissionDbEntity.getHttpMethod()).permissionId(permissionDbEntity.getPermissionId())
					.isAllow(rolePermissionDbEntity.getIsAllow()).build();

			authRecords.add(authEntity);
		}

		return authRecords;

	}

	private String getUidFromAccessToken(String accessToken) throws UnauthorizedException {
//		System.out.println("=====checkAccessToken======");
//		System.out.println(accessToken);
		Claims result;
		try {
			result = Jwts.parser().setSigningKey(SecurityConfig.ACCESS_SECRET.getBytes()).parseClaimsJws(accessToken)
					.getBody();
//			System.out.println(new Gson().toJson(result));

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

//	
}

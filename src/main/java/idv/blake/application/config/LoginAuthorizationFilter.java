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

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.google.gson.Gson;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;
import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.auth.TokenDbEntity;
import idv.blake.application.model.entity.permission.PermissionDbEntity;
import idv.blake.application.model.entity.permission.RolePermissionDbEntity;
import idv.blake.application.model.entity.security.AuthRecordData;
import idv.blake.application.model.entity.security.AuthRecordListData;
import idv.blake.application.model.exception.UnauthorizedException;
import idv.blake.application.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LoginAuthorizationFilter extends BasicAuthenticationFilter {

	private AccountDao accountDao;
	private RolePermissionDao rolePermissionDao;
	private TokenDao tokenDao;

	private StringRedisTemplate stringRedisTemplate;

	public LoginAuthorizationFilter(AuthenticationManager authenticationManager, AccountDao accountDao,
			TokenDao tokenDao, RolePermissionDao rolePermissionDao, StringRedisTemplate stringRedisTemplate) {
		super(authenticationManager);
		this.accountDao = accountDao;
		this.tokenDao = tokenDao;
		this.rolePermissionDao = rolePermissionDao;
		this.stringRedisTemplate = stringRedisTemplate;
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

			AuthRecordListData authRecords = getPermissionFormUid(uidString);
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

	@Transactional
	private AuthRecordListData getPermissionFormUid(String uid) throws UnauthorizedException {

		String cacheKey = String.format("permission-%s", uid);

		String cache = stringRedisTemplate.opsForValue().get(cacheKey);
		if (!StringUtil.isEmpty(cache)) {

			AuthRecordListData recordListData = new Gson().fromJson(cache, AuthRecordListData.class);

			if (recordListData.getExpiredTime() > System.currentTimeMillis()) {
				return recordListData;
			}
		}

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

		List<AuthRecordData> authRecords = new ArrayList<>();

		for (RolePermissionDbEntity rolePermissionDbEntity : rolePermissionDbEntities) {

			PermissionDbEntity permissionDbEntity = rolePermissionDbEntity.getPermission();

			AuthRecordData authEntity = AuthRecordData.builder().apiUrl(permissionDbEntity.getApiUrl())
					.httpMethod(permissionDbEntity.getHttpMethod()).permissionId(permissionDbEntity.getPermissionId())
					.isAllow(rolePermissionDbEntity.getIsAllow()).build();

			authRecords.add(authEntity);
		}

		AuthRecordListData recordListData = AuthRecordListData.builder().authRecordDatas(authRecords)
				.expiredTime(System.currentTimeMillis() + SecurityConfig.PERMISSION_REDIS_EXPIRATION_TIME).build();
		stringRedisTemplate.opsForValue().set(cacheKey, new Gson().toJson(recordListData));

		return recordListData;

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

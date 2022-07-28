package idv.blake.application.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.entity.auth.TokenDbEntity;
import idv.blake.application.model.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LoginAuthorizationFilter extends BasicAuthenticationFilter {

	private AccountDao accountDao;
	private TokenDao tokenDao;

	public LoginAuthorizationFilter(AuthenticationManager authenticationManager, AccountDao accountDao,
			TokenDao tokenDao) {
		super(authenticationManager);
		this.accountDao = accountDao;
		this.tokenDao = tokenDao;
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
			checkAccessToken(accessToken.replace(SecurityConfig.ACCESS_PREFIX, ""));
		} catch (UnauthorizedException e) {
			response.sendError(401, e.getMessage());
			return;
		}

		chain.doFilter(request, response);
	}

	private void checkAccessToken(String accessToken) throws UnauthorizedException {
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

	}

//	private UsernamePasswordAuthenticationToken getAuthentication(String header) {
//		
//		
//		
////        System.out.println("LoginAuthorizationFilter getAuthentication token: " + header);
//		String account = null;
//		try {
//			account = Jwts.parser().setSigningKey(SecurityConfig.ACCESS_SECRET.getBytes())
//					.parseClaimsJws(header.replace(SecurityConfig.ACCESS_PREFIX, "")).getBody().getSubject();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		List<GrantedAuthority> authorities = new ArrayList<>();
//		if (!StringUtil.isEmpty(account)) {
//			List<PermissionDo> permissionDoList = permissionRepository.findByAccount(account);
//			for (PermissionDo permissionDo : permissionDoList) {
//				authorities.add(new SimpleGrantedAuthority(permissionDo.getCode()));
//			}
//		}
//
//		AccountDo accountDo = accountRepository.findByAccount(account);
//		AuthAccountDo authAccountDo = null;
//		if (accountDo != null) {
//			authAccountDo = new AuthAccountDo();
//			authAccountDo.setAccount(accountDo.getAccount());
//			authAccountDo.setId(accountDo.getId());
//			authAccountDo.setName(accountDo.getName());
//		}
//
//		return new UsernamePasswordAuthenticationToken(authAccountDo, null, authorities);
//	}
}

package idv.blake.application.model.service.auth;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import idv.blake.application.config.SecurityConfig;
import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.RefreshTokenDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.entity.ResponseEntity;
import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.auth.LoginRequestEntity;
import idv.blake.application.model.entity.auth.LoginResponseEntity;
import idv.blake.application.model.entity.auth.RefreshTokenDbEntity;
import idv.blake.application.model.entity.auth.RefreshTokenRequestEntity;
import idv.blake.application.model.entity.auth.RegisterRequestEntity;
import idv.blake.application.model.entity.auth.TokenDbEntity;
import idv.blake.application.model.entity.permission.PermissionDbEntity;
import idv.blake.application.model.entity.permission.RolePermissionDbEntity;
import idv.blake.application.model.entity.security.AuthRecordData;
import idv.blake.application.model.entity.security.AuthRecordListData;
import idv.blake.application.model.exception.AlreadyExistExceptionException;
import idv.blake.application.model.exception.InvalidArgumentException;
import idv.blake.application.model.exception.UnauthorizedException;
import idv.blake.application.util.EncodeUtil;
import idv.blake.application.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Service("AuthService")
@Scope("prototype")
public class AuthService {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private RefreshTokenDao refreshTokenDao;

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 註冊
	 * 
	 * @param requestEntity
	 * @return
	 * @throws InvalidArgumentException       參數錯誤
	 * @throws AlreadyExistExceptionException 註冊帳號已存在
	 */
	@Transactional
	public ResponseEntity<LoginResponseEntity> register(RegisterRequestEntity requestEntity)
			throws InvalidArgumentException, AlreadyExistExceptionException {

		if (requestEntity == null || StringUtil.isEmpty(requestEntity.getAccount(), requestEntity.getPassword())) {
			throw new InvalidArgumentException("invalid argument exception");
		}

		if (requestEntity.getAccount().length() > 20) {
			throw new InvalidArgumentException("account length > 20");
		}

		if (!StringUtil.isPasswordValid(requestEntity.getPassword())) {
			throw new InvalidArgumentException("password format invail");
		}

		AccountDbEntity accountDbEntity = accountDao.findByAccount(requestEntity.getAccount());
		if (accountDbEntity != null) {
			throw new AlreadyExistExceptionException(
					String.format("account [%s] alredy exist!!", requestEntity.getAccount()));
		}

		long currentTime = System.currentTimeMillis();
		Timestamp currenTimestamp = new Timestamp(currentTime);

		accountDbEntity = new AccountDbEntity();
		accountDbEntity.setAccount(requestEntity.getAccount());
		accountDbEntity.setPassword(requestEntity.getPassword());
		accountDbEntity.setUid(EncodeUtil.generateUUID());
		accountDbEntity.setCreateDate(currenTimestamp);

		TokenDbEntity tokenDbEntity = TokenDbEntity.builder().account(accountDbEntity).tokenId(generateTokenId())
				.createDate(currenTimestamp)
				.expireDate(new Timestamp(currentTime + SecurityConfig.ACCESS_EXPIRATION_TIME)).build();

		RefreshTokenDbEntity refreshTokenDbEntity = RefreshTokenDbEntity.builder().account(accountDbEntity)
				.tokenId(generateTokenId()).createDate(currenTimestamp)
				.expireDate(new Timestamp(System.currentTimeMillis() + SecurityConfig.REFRESH_EXPIRATION_TIME)).build();

		accountDao.save(accountDbEntity);
		tokenDao.save(tokenDbEntity);
		refreshTokenDao.save(refreshTokenDbEntity);

		LoginResponseEntity responseEntity = LoginResponseEntity.builder().uid(accountDbEntity.getUid())
				.account(accountDbEntity.getAccount())
				.accessToken(generateAccessToken(accountDbEntity.getUid(), tokenDbEntity.getTokenId(),
						tokenDbEntity.getExpireDate().getTime()))
				.refreshToken(generateRefreshToken(accountDbEntity.getUid(), refreshTokenDbEntity.getTokenId(),
						refreshTokenDbEntity.getExpireDate().getTime()))
				.build();

		return new ResponseEntity<LoginResponseEntity>(200, "Success", responseEntity);
	}

	private String generateTokenId() {
		String uuid = EncodeUtil.generateUUID();
		return EncodeUtil.encryptByMd5(uuid);
	}

	private String generateAccessToken(String uid, String tokenId, long expired_time) {

		return Jwts.builder().claim("uid", uid).claim("tokenId", tokenId).setExpiration(new Date(expired_time))
				.signWith(SignatureAlgorithm.HS512, SecurityConfig.ACCESS_SECRET.getBytes()).compact();
	}

	private String generateRefreshToken(String uid, String tokenId, long expired_time) {
		return Jwts.builder().claim("uid", uid).claim("tokenId", tokenId).setExpiration(new Date(expired_time))
				.signWith(SignatureAlgorithm.HS512, SecurityConfig.REFRESH_SECRET.getBytes()).compact();
	}

	/**
	 * 
	 * 登入
	 * 
	 * @param requestEntity
	 * @return
	 * @throws InvalidArgumentException
	 * @throws UnauthorizedException
	 */
	@Transactional
	public ResponseEntity<LoginResponseEntity> login(LoginRequestEntity requestEntity)
			throws InvalidArgumentException, UnauthorizedException {

		if (requestEntity == null || StringUtil.isEmpty(requestEntity.getAccount(), requestEntity.getPassword())) {
			throw new InvalidArgumentException("invalid argument exception");
		}

		AccountDbEntity accountDbEntity = accountDao.findByAccount(requestEntity.getAccount());
		if (accountDbEntity == null) {
			throw new UnauthorizedException(String.format("account [%s] not exist!!", requestEntity.getAccount()));
		}

		if (!accountDbEntity.getPassword().equals(requestEntity.getPassword())) {
			throw new UnauthorizedException(String.format("account [%s] password fail!", requestEntity.getAccount()));
		}

		long currentTime = System.currentTimeMillis();
		Timestamp currenTimestamp = new Timestamp(currentTime);

		TokenDbEntity tokenDbEntity = TokenDbEntity.builder().account(accountDbEntity).tokenId(generateTokenId())
				.createDate(currenTimestamp)
				.expireDate(new Timestamp(currentTime + SecurityConfig.ACCESS_EXPIRATION_TIME)).build();

		RefreshTokenDbEntity refreshTokenDbEntity = RefreshTokenDbEntity.builder().account(accountDbEntity)
				.tokenId(generateTokenId()).createDate(currenTimestamp)
				.expireDate(new Timestamp(System.currentTimeMillis() + SecurityConfig.REFRESH_EXPIRATION_TIME)).build();

		tokenDao.deleteByAccount(accountDbEntity);
		refreshTokenDao.deleteByAccount(accountDbEntity);
		tokenDao.save(tokenDbEntity);
		refreshTokenDao.save(refreshTokenDbEntity);

		LoginResponseEntity responseEntity = LoginResponseEntity.builder().uid(accountDbEntity.getUid())
				.account(accountDbEntity.getAccount())
				.accessToken(generateAccessToken(accountDbEntity.getUid(), tokenDbEntity.getTokenId(),
						tokenDbEntity.getExpireDate().getTime()))
				.refreshToken(generateRefreshToken(accountDbEntity.getUid(), refreshTokenDbEntity.getTokenId(),
						refreshTokenDbEntity.getExpireDate().getTime()))
				.build();

		return new ResponseEntity<LoginResponseEntity>(200, "Success", responseEntity);
	}

	/**
	 * 刷新token
	 * 
	 * @return
	 * @throws InvalidArgumentException
	 * @throws UnauthorizedException
	 */
	@Transactional
	public ResponseEntity<LoginResponseEntity> refreshToken(RefreshTokenRequestEntity requestEntity)
			throws InvalidArgumentException, UnauthorizedException {
		if (requestEntity == null || StringUtil.isEmpty(requestEntity.getRefreshToken())) {
			throw new InvalidArgumentException("invalid argument exception");
		}

		Claims claims = getClaimFromJWT(requestEntity.getRefreshToken());

		String refreshTokenId = claims.get("tokenId", String.class);
		String uid = claims.get("uid", String.class);

		checkRefreshToken(refreshTokenId);

		AccountDbEntity accountDbEntity = accountDao.findByUid(uid);
		if (accountDbEntity == null) {
			throw new UnauthorizedException(String.format("uid [%s] not exist!!", uid));
		}

		long currentTime = System.currentTimeMillis();
		Timestamp currenTimestamp = new Timestamp(currentTime);

		TokenDbEntity tokenDbEntity = TokenDbEntity.builder().account(accountDbEntity).tokenId(generateTokenId())
				.createDate(currenTimestamp)
				.expireDate(new Timestamp(currentTime + SecurityConfig.ACCESS_EXPIRATION_TIME)).build();

		RefreshTokenDbEntity refreshTokenDbEntity = RefreshTokenDbEntity.builder().account(accountDbEntity)
				.tokenId(generateTokenId()).createDate(currenTimestamp)
				.expireDate(new Timestamp(System.currentTimeMillis() + SecurityConfig.REFRESH_EXPIRATION_TIME)).build();

		tokenDao.deleteByAccount(accountDbEntity);
		refreshTokenDao.deleteByAccount(accountDbEntity);
		tokenDao.save(tokenDbEntity);
		refreshTokenDao.save(refreshTokenDbEntity);

		LoginResponseEntity responseEntity = LoginResponseEntity.builder().uid(accountDbEntity.getUid())
				.account(accountDbEntity.getAccount())
				.accessToken(generateAccessToken(accountDbEntity.getUid(), tokenDbEntity.getTokenId(),
						tokenDbEntity.getExpireDate().getTime()))
				.refreshToken(generateRefreshToken(accountDbEntity.getUid(), refreshTokenDbEntity.getTokenId(),
						refreshTokenDbEntity.getExpireDate().getTime()))
				.build();

		return new ResponseEntity<LoginResponseEntity>(200, "Success", responseEntity);
	}

	private void checkRefreshToken(String refreshTokenId) throws UnauthorizedException {
		RefreshTokenDbEntity refreshTokenDbEntity = refreshTokenDao.findByTokenId(refreshTokenId);
		if (refreshTokenDbEntity == null) {
			throw new UnauthorizedException("refresh token invalid !!");
		}

		if (refreshTokenDbEntity.getExpireDate().getTime() < System.currentTimeMillis()) {
			throw new UnauthorizedException("refresh token invalid !!");
		}

	}

	private Claims getClaimFromJWT(String jwt) throws UnauthorizedException {

		try {
			Claims result = Jwts.parser().setSigningKey(SecurityConfig.REFRESH_SECRET.getBytes()).parseClaimsJws(jwt)
					.getBody();

			return result;

		} catch (SignatureException e) {
			throw new UnauthorizedException(e.getMessage());
		}

	}

	/**
	 * 回傳權限資料by Uid
	 * 
	 * 
	 * @param uid
	 * @return
	 * @throws UnauthorizedException
	 */
	@Transactional
	public AuthRecordListData getPermission(String uid) throws UnauthorizedException {

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

		List<RolePermissionDbEntity> rolePermissionDbEntities = accountDbEntity.getAccountRole().getRole()
				.getRolePermission();

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
}

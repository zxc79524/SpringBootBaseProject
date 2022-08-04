package idv.blake.application.controller.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import idv.blake.application.BaseUnitTest;
import idv.blake.application.config.LoginAuthorizationFilter;
import idv.blake.application.config.SecurityConfig;
import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;
import idv.blake.application.model.entity.ResponseEntity;
import idv.blake.application.model.entity.auth.LoginResponseEntity;
import idv.blake.application.model.entity.auth.RefreshTokenRequestEntity;
import idv.blake.application.model.entity.auth.RegisterRequestEntity;
import idv.blake.application.util.EncodeUtil;

class AuthControllerTest extends BaseUnitTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	public static String REGISTER_URL = SecurityConfig.AUTH_URL + "/register";
	public static String LOGIN_URL = SecurityConfig.AUTH_URL + "/login";
	public static String REFRESH_TOKEN_URL = SecurityConfig.AUTH_URL + "/refresh";

	public static String CHECK_TOKEN_URL = "/check/token";

	private MockMvc mvc;
	private MockMvc loginMvc;

	@BeforeAll
	public static void beforeClass() {

		System.out.println("@BeforeClass");
	}

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		loginMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilter(new LoginAuthorizationFilter(new AuthenticationManager() {

					@Override
					public Authentication authenticate(Authentication authentication) throws AuthenticationException {
						// TODO Auto-generated method stub
						return null;
					}
				}, accountDao, tokenDao, rolePermissionDao)).build();
	}

	@Test
	void login() throws Exception {
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("root", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("user", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
	}

	@Test
	void test() throws Exception {

		// 註冊時的密碼,沒有轉換為SHA256
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", "Aa123456"))))
				.andExpect(status().is(400)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 註冊時的密碼,沒有轉換為SHA256 小寫

		System.out
				.println(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456").toUpperCase())));

		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(
								new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456").toUpperCase()))))
				.andExpect(status().is(400)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 註冊成功
		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 註冊已存在的帳號
		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(409)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 帳號不存在
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom1", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(401)).andReturn();

		// 帳號存在密碼錯誤
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON).content(
								toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456gewgewhg")))))
				.andExpect(status().is(401)).andReturn();

		// 參數錯誤
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity(null, EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(400)).andReturn();

		// 登入成功
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		ResponseEntity<LoginResponseEntity> responseEntity = fromJsonForResponseEntity(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8));

//		getRefreshTokenIdFromJWT(responseEntity.getResultData().getRefreshToken() + "aaaa");

		// 刷新Token
		result = mvc.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
				.header("Content-TYPE", MediaType.APPLICATION_JSON).content(toJson(new RefreshTokenRequestEntity())))
				.andExpect(status().is(400)).andReturn();
		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 測試錯誤的refresh token
		result = mvc
				.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RefreshTokenRequestEntity(
								responseEntity.getResultData().getRefreshToken() + "aa"))))
				.andExpect(status().is(401)).andReturn();

		// 測試正確的refresh token
		result = mvc
				.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(
								new RefreshTokenRequestEntity(responseEntity.getResultData().getRefreshToken()))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 測試token 是否正常驗證

		result = loginMvc.perform(MockMvcRequestBuilders.get(CHECK_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
				.header("Content-TYPE", MediaType.APPLICATION_JSON)).andExpect(status().is(401)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// 刪除建立的帳號
		accountDao.deleteByAccount("Tom");
	}

	private ResponseEntity<LoginResponseEntity> fromJsonForResponseEntity(String json) {
		Type listType = new TypeToken<ResponseEntity<LoginResponseEntity>>() {
		}.getType();

		ResponseEntity<LoginResponseEntity> result = new Gson().fromJson(json, listType);

		return result;
	}

}

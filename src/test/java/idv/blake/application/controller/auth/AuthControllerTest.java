package idv.blake.application.controller.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import idv.blake.application.BaseUnitTest;
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

	@BeforeAll
	public static void beforeClass() {

		System.out.println("@BeforeClass");
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

		// ??????????????????,???????????????SHA256
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", "Aa123456"))))
				.andExpect(status().is(400)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ??????????????????,???????????????SHA256 ??????

		System.out
				.println(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456").toUpperCase())));

		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(
								new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456").toUpperCase()))))
				.andExpect(status().is(400)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ????????????????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(REGISTER_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(409)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ???????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom1", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(401)).andReturn();

		// ????????????????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON).content(
								toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456gewgewhg")))))
				.andExpect(status().is(401)).andReturn();

		// ????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity(null, EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(400)).andReturn();

		// ????????????
		result = mvc
				.perform(MockMvcRequestBuilders.post(LOGIN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", EncodeUtil.encryptSHA256("Aa123456")))))
				.andExpect(status().is(200)).andReturn();

		ResponseEntity<LoginResponseEntity> responseEntity = fromJsonForResponseEntity(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8));

//		getRefreshTokenIdFromJWT(responseEntity.getResultData().getRefreshToken() + "aaaa");

		// ??????Token
		result = mvc.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
				.header("Content-TYPE", MediaType.APPLICATION_JSON).content(toJson(new RefreshTokenRequestEntity())))
				.andExpect(status().is(400)).andReturn();
		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ???????????????refresh token
		result = mvc
				.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RefreshTokenRequestEntity(
								responseEntity.getResultData().getRefreshToken() + "aa"))))
				.andExpect(status().is(401)).andReturn();

		// ???????????????refresh token
		result = mvc
				.perform(MockMvcRequestBuilders.post(REFRESH_TOKEN_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(
								new RefreshTokenRequestEntity(responseEntity.getResultData().getRefreshToken()))))
				.andExpect(status().is(200)).andReturn();

		System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));

		// ?????????????????????
		accountDao.deleteByAccount("Tom");
	}

	private ResponseEntity<LoginResponseEntity> fromJsonForResponseEntity(String json) {
		Type listType = new TypeToken<ResponseEntity<LoginResponseEntity>>() {
		}.getType();

		ResponseEntity<LoginResponseEntity> result = new Gson().fromJson(json, listType);

		return result;
	}

}

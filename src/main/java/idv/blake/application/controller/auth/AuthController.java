package idv.blake.application.controller.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import idv.blake.application.config.SecurityConfig;
import idv.blake.application.model.entity.ResponseEntity;
import idv.blake.application.model.entity.auth.LoginRequestEntity;
import idv.blake.application.model.entity.auth.LoginResponseEntity;
import idv.blake.application.model.entity.auth.RefreshTokenRequestEntity;
import idv.blake.application.model.entity.auth.RegisterRequestEntity;
import idv.blake.application.model.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = SecurityConfig.AUTH_URL)
@Tag(name = "驗證")
public class AuthController {

	@Autowired
	@Resource(name = "AuthService")
	private AuthService authService;

	@Operation(summary = "註冊", description = "註冊新帳號")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "400", description = "參數錯誤", content = { @Content() }),
			@ApiResponse(responseCode = "421", description = "帳號已被註冊", content = { @Content() }) })

	@RequestMapping(value = "/register", method = { RequestMethod.POST })
	public ResponseEntity<LoginResponseEntity> register(@RequestBody RegisterRequestEntity requestEntity,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return authService.register(requestEntity);

	}

	@Operation(summary = "登入", description = "註冊新帳號")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "401", description = "帳號不存在或密碼錯誤", content = { @Content() }),
			@ApiResponse(responseCode = "421", description = "帳號已被註冊並啟用", content = { @Content() }) })

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public ResponseEntity<LoginResponseEntity> login(@RequestBody @Valid LoginRequestEntity requestEntity,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return authService.login(requestEntity);

	}

	@Operation(summary = "刷新token", description = "使用refresh token 取得新的Access Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "401", description = "Refresh Token 無效", content = { @Content() }) })

	@RequestMapping(value = "/refresh", method = { RequestMethod.POST })
	public ResponseEntity<LoginResponseEntity> refresh(@RequestBody RefreshTokenRequestEntity requestEntity)
			throws Exception {

		return authService.refreshToken(requestEntity);
	}

}

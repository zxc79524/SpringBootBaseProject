package idv.blake.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.auth.TokenDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;

@EnableWebSecurity
public class LoginWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.GET, "/swagger-ui/**", "/swagger-ui/index.html", "/v3/**")
				.antMatchers(HttpMethod.GET, "/check").antMatchers(HttpMethod.POST, SecurityConfig.AUTH_URL + "/login",
						SecurityConfig.AUTH_URL + "/register", SecurityConfig.AUTH_URL + "/refresh_token");

	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.cors().and().csrf().disable().exceptionHandling()
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
				.addFilter(new LoginAuthorizationFilter(authenticationManager(), accountDao, tokenDao,
						rolePermissionDao, stringRedisTemplate))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//	        System.out.println("LoginWebSecurityConfig configure http security");
//		httpSecurity.cors().and().csrf().disable().exceptionHandling()
//				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and().authorizeRequests()
//				.antMatchers(HttpMethod.POST, SecurityConfig.AUTH_URL + "/login").permitAll()
//				.antMatchers(HttpMethod.POST, SecurityConfig.AUTH_URL + "/register").permitAll()
//				.antMatchers(HttpMethod.POST, SecurityConfig.AUTH_URL + "/refresh_token").permitAll()
//				.antMatchers(HttpMethod.GET, "/check").permitAll()
//				.antMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
//				.antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/v3/**").permitAll()
//				.anyRequest().authenticated().and()
//				.addFilter(new LoginAuthorizationFilter(authenticationManager())).sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}
}

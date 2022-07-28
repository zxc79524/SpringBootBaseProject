package idv.blake.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Security API", version = "1.0", description = "安全驗證API"))
public class SpringBootBaseProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBaseProjectApplication.class, args);
	}

//	@GetMapping("/")
//	public String home() {
//		return "Welcome!";
//	}

}

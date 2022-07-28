package idv.blake.application.controller.permission;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import idv.blake.application.BaseUnitTest;
import idv.blake.application.model.entity.auth.RegisterRequestEntity;

class PermissoinControllerTest extends BaseUnitTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	public static String PERMISSION_URL = "/permission";

	@Test
	void testAddPermission() throws Exception {

		MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.get(PERMISSION_URL).accept(MediaType.APPLICATION_JSON)
						.header("Content-TYPE", MediaType.APPLICATION_JSON)
						.content(toJson(new RegisterRequestEntity("Tom", "Aa123456"))))
				.andExpect(status().is(400)).andReturn();
	}

}

package idv.blake.application;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootBaseProjectApplication.class)
@WebAppConfiguration
public class BaseUnitTest {

	public static String toJson(Object obj) {
		return new Gson().toJson(obj);
	}

	public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return new Gson().fromJson(json, classOfT);
	}

}

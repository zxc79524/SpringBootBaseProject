package idv.blake.application.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class EncodeUtilTest {

	@Test
	void testEncryptSHA256() {
		String result = EncodeUtil.encryptSHA256("unittestcheck");

//		System.out.println(result.length());
		assertEquals(result, "a5f46e3fa3f3df0268c12a58c6d06427da6d324433525af1b46b8a58229bad3e");
	}

}

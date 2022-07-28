package idv.blake.application.util;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class StringUtilTest {

	@Test
	void testIsPasswordValid() {

		assertEquals(StringUtil.isPasswordValid("a5f46e3fa3f3df0268c12a58c6d06427da6d324433525af1b46b8a58229bad3e"),
				true);

		assertEquals(StringUtil.isPasswordValid("a5f46e3fa3f3df0268c12a58c6d06427da6d324433525af1b46b8a58229bad3E"),
				false);

		assertEquals(StringUtil.isPasswordValid("a5f46e3fa3f3df0268c12a58c6d06427da6d324433525af1b46b8a58229bad3"),
				false);
		
		assertEquals(StringUtil.isPasswordValid("a5f46e3fa3f3df0268c12a58c6d06427da6d324433525af1b46b8a58229bad3-"),
				false);

	}

}

package idv.blake.application.util;

import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmpty(String parameter) {
		return parameter == null || (parameter.trim().length() == 0);
	}

	public static boolean isEmpty(String... values) {

		for (String value : values) {
			if (isEmpty(value)) {
				return true;
			}

		}

		return false;
	}

	public static boolean isEmail(String email) {
		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		return email.matches(regex);
	}

	/**
	 * 
	 * 檢查密碼是否符合SHA256格式
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isPasswordValid(String input) {
		if (isEmpty(input)) {
			return false;
		}

		if (input.length() != 64) {
			return false;
		}

		return Pattern.matches("[0-9a-z]{64}", input);
	}

}

package idv.blake.application.util;

import java.security.MessageDigest;
import java.util.UUID;

public class EncodeUtil {

	public static String encrypt(String s, String encodeType) {
		MessageDigest sha = null;

		try {
			sha = MessageDigest.getInstance(encodeType);
			sha.update(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return byte2hex(sha.digest());

	}

	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}

	public static String encryptByMd5(String text) {

		return encrypt(text, "MD5");

	}

	public static String encryptSHA256(String text) {

		return encrypt(text, "SHA256");

	}

	public static String generateUUID() {

		UUID uuid = UUID.randomUUID();

		return uuid.toString();

	}

}

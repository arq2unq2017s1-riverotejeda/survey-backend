package unq.utils;

import java.security.SecureRandom;

/**
 * Created by mrivero on 26/11/16.
 */
public class SecurityTokenGenerator {

	public static String getToken() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes.toString();
	}
}

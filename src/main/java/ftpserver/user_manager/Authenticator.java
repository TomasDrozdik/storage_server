package ftpserver.user_manager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Authenticator {
	private static SecretKeyFactory factory;

	static {
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static String hashPasswd(String password, long salt) throws InvalidKeySpecException {

		KeySpec spec = new PBEKeySpec(password.toCharArray(), ByteBuffer.allocate(Long.BYTES).putLong(salt).array(),
				65536, 128);

		return new String(factory.generateSecret(spec).getEncoded());
	}
}

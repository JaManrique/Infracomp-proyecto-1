import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ManejadorRSA {

	public static String descifrar(Key pKey, String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] clearText = msg.getBytes();
		cipher.init(Cipher.DECRYPT_MODE, pKey);
		byte[] cipheredText = cipher.doFinal(clearText);
		return new String(cipheredText);

	}

	public static String cifrar(Key pKey, String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] clearText = msg.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, pKey);
		byte[] cipheredText = cipher.doFinal(clearText);
		return new String(cipheredText);
	}

}

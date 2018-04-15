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
import javax.xml.bind.DatatypeConverter;

public class ManejadorRSA {

	public static byte[] descifrar(Key pKey, String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("key: " + pKey.toString() + " // msg: " + msg);
		Cipher cipher = Cipher.getInstance("RSA");
		//byte[] clearText = msg;
		cipher.init(Cipher.DECRYPT_MODE, pKey);
		System.out.println(DatatypeConverter.parseHexBinary(msg));
		byte[] cipheredText = cipher.doFinal(DatatypeConverter.parseHexBinary(msg));
		return cipheredText;
	}

	public static String cifrar(Key pKey, byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] clearText = msg;
		cipher.init(Cipher.ENCRYPT_MODE, pKey);
		byte[] cipheredText = cipher.doFinal(clearText);
		return DatatypeConverter.printHexBinary(cipheredText);
	}

}

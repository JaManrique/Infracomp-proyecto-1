import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class ManejadorAES {

	public static String cifrar(Key pKey, String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		byte[] clearText = msg.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, pKey);
		byte[] cipheredText = cipher.doFinal(clearText);
		return DatatypeConverter.printHexBinary(cipheredText);
	}

}

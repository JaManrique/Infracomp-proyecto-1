import java.security.MessageDigest;

public class ManejadorMD5 {

	public static byte[] hash(String msg) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			 md5.update(msg.getBytes());
			 return md5.digest();
			 } catch (Exception e) {
			 return null;
			 }
	}

}

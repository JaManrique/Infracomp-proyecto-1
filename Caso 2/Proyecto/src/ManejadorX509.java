import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class ManejadorX509 {

	private X509Certificate cert;
	
	public ManejadorX509(){
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(2048);
		
		//Llave para que se va a crear el CD
		KeyPair key = keyGen.generateKeyPair();
		
		//Llave privada del creador del CD (nosotros)
		PrivateKey pr = key.getPrivate();
		PublicKey pb = key.getPublic();
		Date inicio = new Date(); //Current millis
		Date expiracion = new Date(1554849180000l);
		BigInteger serial = new BigInteger("1");
		X509Certificate caCert = null;
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal subjectName = new X500Principal("CN=js.díaz / ja.manrique");
		
		certGen.setNotAfter(expiracion);
		certGen.setNotBefore(inicio);
		certGen.setSerialNumber(serial);
		certGen.setIssuerDN(subjectName);
		certGen.setSubjectDN(subjectName);
		certGen.setPublicKey(pb);
		certGen.setSignatureAlgorithm("SHA1WithRSA");
			
		cert= null;
		try {
			cert = certGen.generate(pr);
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(cert.toString());
		try {
			System.out.println(cert.getTBSCertificate());
			System.out.println(cert.getEncoded());
			System.out.println(cert.getPublicKey().getEncoded());
			cert.verify(pb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public byte[] darCertCliente() {
		try {
			return cert.getEncoded();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new byte[]{};
		} 
	}

	public static boolean verificarCertServidor(byte[] certsrv) {
		return true;
	}

	public static Key extraerLlave(byte[] certsrv) {
		//X509v3CertificateBuilder builder = new X509V3
		return null;
	}

	public static void main(String[] args) {
		new ManejadorX509();
	}
}

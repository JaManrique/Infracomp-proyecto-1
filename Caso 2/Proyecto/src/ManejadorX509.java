import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

public class ManejadorX509 {

	private static final boolean DEBUG = false; 

	private X509Certificate cert;

	public ManejadorX509(){
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(2048);

		//Llave para la que se va a crear el CD
		KeyPair key = keyGen.generateKeyPair();

		//Llave privada del creador del CD (nosotros)
		PrivateKey pr = key.getPrivate();
		PublicKey pb = key.getPublic();
		Date inicio = new Date(); //Inicia => current millis
		Date expiracion = new Date(1554849180000l);//Termina => Abril 2019
		BigInteger serial = new BigInteger("1");// Un número cualquiera

		//Generador de certificados
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		//Crear nombre del expedidor/dueño del certificado (es el mismo)
		X500Principal subjectName = new X500Principal("CN=js.díaz / ja.manrique");

		//Asignar al generador
		certGen.setNotAfter(expiracion);
		certGen.setNotBefore(inicio);
		certGen.setSerialNumber(serial);
		certGen.setIssuerDN(subjectName);
		certGen.setSubjectDN(subjectName);
		certGen.setPublicKey(pb);
		certGen.setSignatureAlgorithm("SHA1WithRSA");

		//Asignar ceritifacdo
		cert= null;
		try {
			cert = certGen.generate(pr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (DEBUG)
			try {
				System.out.println(cert.toString());
				System.out.println(cert.getTBSCertificate());
				System.out.println("Encoded: ");
				System.out.println(cert.getEncoded());
				System.out.println("Decoded: ");
				ASN1InputStream is = new ASN1InputStream(cert.getEncoded());
				ASN1Primitive prim = ASN1Primitive.fromByteArray(cert.getEncoded());
				System.out.println(prim.getEncoded());
				System.out.println(cert.getPublicKey().getEncoded());

				cert.verify(pb);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}	

	public byte[] darCertCliente() {
		try {
			return cert.getEncoded();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
			return new byte[]{};
		} 
	}

	public boolean verificarCertServidor(byte[] certsrv) {
		X509Certificate cert = byteToCert(certsrv);
		try {
			cert.verify(cert.getPublicKey());
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Key extraerLlave(byte[] certsrv) {
		X509Certificate cert = byteToCert(certsrv);
		return cert.getPublicKey();
	}

	private X509Certificate byteToCert(byte[] cert) {
		try {
			System.out.println("-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----"); // no es nulo
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Base64.Decoder dec = Base64.getDecoder();//Haciendo el decode de base 64 tampoco ayuda :(
			Base64.Encoder enc = Base64.getEncoder();
			InputStream in = new ByteArrayInputStream(/*dec.decode(*/cert/*)*/);
			InputStream in1 = new ByteArrayInputStream(enc.encode(("-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----").getBytes()));
			return (X509Certificate) cf.generateCertificate(in1);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		new ManejadorX509();
	}
}

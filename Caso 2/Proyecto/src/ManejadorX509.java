import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import sun.security.pkcs.SignerInfo;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class ManejadorX509 {

	private static final boolean DEBUG = false; 

	private X509Certificate cert;
	private X509CertificateHolder cert1;
	private PrivateKey pr;

	public ManejadorX509(){
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(2048/2);

		//Llave para la que se va a crear el CD
		KeyPair key = keyGen.generateKeyPair();

		//Llave privada del creador del CD (nosotros)
		pr = key.getPrivate();
		PublicKey pb = key.getPublic();		

		Date inicio = new Date(); //Inicia => current millis
		Date expiracion = new Date(1554849180000l);//Termina => Abril 2019
		BigInteger serial = new BigInteger("1");// Un número cualquiera

		//Generador de certificados
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		//Crear nombre del expedidor/dueño del certificado (es el mismo)
		X500Principal subjectName = new X500Principal("CN=js.díaz / ja.manrique");
		X500Name subjectName1 = new X500Name("CN=js.díaz / ja.manrique");

		//Asignar al generador
		certGen.setNotAfter(expiracion);
		certGen.setNotBefore(inicio);
		certGen.setSerialNumber(serial);
		certGen.setIssuerDN(subjectName);
		certGen.setSubjectDN(subjectName);
		certGen.setPublicKey(pb);
		certGen.setSignatureAlgorithm("SHA1WithRSA");

		//nuevo------
		ContentSigner sigGen = null;
		try {
			sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(pr);
		} catch (OperatorCreationException e1) {e1.printStackTrace();} 
		SubjectPublicKeyInfo ski = SubjectPublicKeyInfo.getInstance(pb.getEncoded());
		X509v3CertificateBuilder certGen2 = new X509v3CertificateBuilder(subjectName1, serial, inicio, expiracion, subjectName1, ski);
		//-----------

		//Asignar ceritifacdo
		cert= null;
		cert1 = null;
		try {
			cert = certGen.generate(pr);
			cert1 = certGen2.build(sigGen);
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
			//return cert.getEncoded();
			return cert1.getEncoded();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[]{};
		} 
	}

	public boolean verificarCertServidor(byte[] certsrv) {
		boolean answer = false;
		try {
			System.out.println(certsrv);
			X509CertificateHolder ch = new X509CertificateHolder(certsrv);
			ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder()
					.setProvider("BC").build(ch.getSubjectPublicKeyInfo());

			answer = ch.isSignatureValid(contentVerifierProvider);
		}
		catch (IOException | OperatorCreationException | CertException e) {
			e.printStackTrace();
		}
		return answer;
	}

	public Key extraerLlave(byte[] certsrv) {
		try {
			X509Certificate mancito = null;
			X509CertificateHolder ch = new X509CertificateHolder(certsrv.clone());
			mancito = new JcaX509CertificateConverter().setProvider("BC").getCertificate(ch);
			return mancito.getPublicKey();
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		ManejadorX509 a = new ManejadorX509();
		a.verificarCertServidor(a.cert1.getEncoded());
	}

	public Key darLlavePrivada() {
		return pr;
	}
}

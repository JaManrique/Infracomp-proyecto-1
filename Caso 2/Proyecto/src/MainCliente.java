import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;



public class MainCliente {
	private static final String HOLA = "HOLA";
	private static final String INICIO = "INICIO";
	private static final String ALG = "ALGORITMOS";
	private static final String ESTADO_OK = "ESTADO:OK";
	private static final String ESTADO_ERROR = "ESTADO:ERROR";
	private static final String CERT_CLIENTE = "CERTCLNT";
	private static final String CERT_SERVIDOR = "CERTSRV";
	private static final String ACT1 = "ACT1";
	private static final String ACT2 = "ACT2";

	private static final String AES = "AES";
	private static final String BLOWFISH = "BLOWFISH";
	private static final String RSA = "RSA";
	private static final String MD5 = "HMACMD5";
	private static final String SHA1 = "HMACSHA1";
	private static final String SHA256 = "HMACSHA256";
	
	private static ManejadorX509 X509;

	private String serverIp = "localhost";
	private Key llaveCliente = null;
	private long t1, t2;
	private static int nErrores = 0;
	private static List<Long> listT1 = new ArrayList<>();
	private static List<Long> listT2 = new ArrayList<>();
	
	public MainCliente() {
		X509 = new ManejadorX509();
	}
	
	public void reportarEstado() throws IOException, Exception{
		Socket socket = new Socket(serverIp, 8080);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintWriter pw = new PrintWriter(os, true);
		
		pw.println(HOLA);
		String s = br.readLine();
		if(!INICIO.equals(s)){
			nErrores++;
			cerrarRecursos(socket, br, pw);
			throw new Exception("error al iniciar");
		}
		pw.println(ALG + ":" + AES + ":" + RSA + ":" + MD5);
		
		verificarEstado(br.readLine());
		
		pw.println(CERT_CLIENTE);
		os.write(X509.darCertCliente());
		os.flush();
		
		verificarEstado(br.readLine());
		
		s = br.readLine();
		if(!CERT_SERVIDOR.equals(s)){
			nErrores++;
			cerrarRecursos(socket, br, pw);
			throw new Exception("error certsrv");
		}
		
		byte[] certsrv = new byte[0];
		
		//Wait for bytes to be written on the socket
		while (certsrv.length == 0) {
			//System.out.println("certsv len: " + certsrv.length + " // actual len: " + is.available());
			certsrv = new byte[is.available()];
		}
		
		is.read(certsrv);
		long t1 = System.currentTimeMillis();
		
		if(X509.verificarCertServidor(certsrv)){
			pw.println(ESTADO_OK);
		}
		else{
			pw.println(ESTADO_ERROR);
			nErrores++;
			cerrarRecursos(socket, br, pw);
			throw new Exception("certificado erroneo");
		}

		Key llaveServ = X509.extraerLlave(certsrv);
		t1 = System.currentTimeMillis() - t1;
		listT1.add(t1);
		
		String[] S = br.readLine().split(":");
		
		if(!S[0].equals(INICIO)){
			nErrores++;
			cerrarRecursos(socket, br, pw);
			throw new Exception("error al iniciar");
		}
		byte[] bytesSesion = ManejadorRSA.descifrar(X509.darLlavePrivada(), S[1]);
		Key llaveSesion = new SecretKeySpec(bytesSesion, 0, bytesSesion.length, "AES");
		
		String pos = "41 24.2028, 2 10.4418";
		long t2 = System.currentTimeMillis();
		pw.println(ACT1 + ":" + ManejadorAES.cifrar(llaveSesion, pos));
		pw.println(ACT2 + ":" + ManejadorRSA.cifrar(llaveServ, ManejadorMD5.hash(pos)));
		
		verificarEstado(br.readLine());
		t2 = System.currentTimeMillis() - t2;
		listT2.add(t2);
		
		cerrarRecursos(socket, br, pw);

	}

	private void cerrarRecursos(Socket socket, BufferedReader br, PrintWriter pw) throws IOException {
		pw.close();
		br.close();
		socket.close();
	}

	private void verificarEstado(String S) throws Exception{
		if(ESTADO_OK.equals(S)){
			return;
		}
		else if(ESTADO_ERROR.equals(S)){
			nErrores++;
			throw new Exception("estado error");
		}
		else{
			nErrores++;
			throw new Exception("error comunicacion");
		}
	}
	
	public static void main(String[] args) {
		try {
			new MainCliente().reportarEstado();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

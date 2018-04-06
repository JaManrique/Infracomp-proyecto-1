import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;



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
	private static final String CMD5 = "HMACMD5";
	private static final String CSHA1 = "HMACSHA1";
	private static final String CSHA256 = "HMACSHA256";

	private String serverIp = "localhost";
	private byte[] llaveCliente = null;
	
	public void reportarEstado() throws IOException, Exception{
		Socket socket = new Socket(serverIp, 8080);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintWriter pw = new PrintWriter(os);
		
		pw.write(HOLA);
		
		String s = br.readLine();
		if(!s.equals(INICIO)){
			throw new Exception("error al iniciar");
		}
		pw.write(ALG + ":" + AES + ":" + RSA + ":" + CMD5);
		
		verificarEstado(br.readLine());
		
		pw.write(CERT_CLIENTE);
		os.write(ManejadorX509.darCertCliente());
		os.flush();
		
		verificarEstado(br.readLine());
		
		s = br.readLine();
		if(!s.equals(CERT_SERVIDOR)){
			throw new Exception("error certsrv");
		}
		
		byte[] certsrv = new byte[is.available()];
		is.read(certsrv);
		
		if(ManejadorX509.verificarCertServidor(certsrv)){
			pw.write(ESTADO_OK);
		}
		else{
			pw.write(ESTADO_ERROR);
			throw new Exception("certificado erroneo");
		}

		byte[] llaveServ = ManejadorX509.extraerLlave(certsrv);
		
		String[] S = br.readLine().split(":");
		if(!S[0].equals(INICIO)){
			throw new Exception("error al iniciar");
		}
		byte[] llaveSesion = ManejadorRSA.descifrar(llaveServ, S[1]);
		
		String pos = "41 24.2028, 2 10.4418";
		pw.write(ACT1 + ":" + ManejadorAES.cifrar(llaveSesion, pos));
		pw.write(ACT2 + ":" + ManejadorRSA.cifrar(llaveCliente, ManejadorMD5.hash(pos)));
		
		verificarEstado(br.readLine());

	}

	private void verificarEstado(String S) throws Exception{
		if(S.equals(ESTADO_OK)){
			return;
		}
		else if(S.equals(ESTADO_ERROR)){
			throw new Exception("estado error");
		}
		else{
			throw new Exception("error comunicacion");
		}
	}
}

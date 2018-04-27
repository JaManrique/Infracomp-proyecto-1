import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;




public class MainCliente extends Thread{
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

	private static final boolean CAMBIAR_THREAD = true;
	
	private static ManejadorX509 X509;

	private String serverIp = "25.5.63.71"; //IP de Hamachi
	private Key llaveCliente = null;
	private long t1, t2;
	private boolean error;
	private static String log = "./data/log.txt";
	private static String params = "./data/params.txt";

	public MainCliente() {
		X509 = new ManejadorX509();
		t1 = 0;
		t2 = 0;
		error = false;
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
			error = true;
			cerrarRecursos(socket, br, pw);
			throw new Exception("error al iniciar");
		}
		pw.println(ALG + ":" + AES + ":" + RSA + ":" + MD5);

		verificarEstado(br.readLine());

		pw.println(CERT_CLIENTE);
//		pw.println(X509.darCertCliente());
		os.write(X509.darCertCliente());
		os.flush();

		verificarEstado(br.readLine());

		s = br.readLine();
		if(!CERT_SERVIDOR.equals(s)){
			error = true;
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
		long t = System.currentTimeMillis();

		if(X509.verificarCertServidor(certsrv)){
			pw.println(ESTADO_OK);
		}
		else{
			pw.println(ESTADO_ERROR);
			error = true;
			cerrarRecursos(socket, br, pw);
			throw new Exception("certificado erroneo");
		}

		Key llaveServ = X509.extraerLlave(certsrv);
		t1 = System.currentTimeMillis() - t;

		String[] S = br.readLine().split(":");

		if(!S[0].equals(INICIO)){
			error = true;
			cerrarRecursos(socket, br, pw);
			throw new Exception("error al iniciar");
		}
		byte[] bytesSesion = ManejadorRSA.descifrar(X509.darLlavePrivada(), S[1]);
		Key llaveSesion = new SecretKeySpec(bytesSesion, 0, bytesSesion.length, "AES");

		String pos = "41 24.2028, 2 10.4418";
		t = System.currentTimeMillis();
		pw.println(ACT1 + ":" + ManejadorAES.cifrar(llaveSesion, pos));
		pw.println(ACT2 + ":" + ManejadorRSA.cifrar(llaveServ, ManejadorMD5.hash(pos)));

		verificarEstado(br.readLine());
		t2 = System.currentTimeMillis() - t;

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
			error = true;
			throw new Exception("estado error");
		}
		else{
			error = true;
			throw new Exception("error comunicacion");
		}
	}

	public static void main(String[] args) {
		try {
			int nIteraciones = 0, rampUp = 0;
			Properties p = new Properties();
			p.load(new FileReader(params));
			nIteraciones = Integer.parseInt(p.getProperty("nIteraciones"));
			rampUp = Integer.parseInt(p.getProperty("rampUp"));
			
			long start = System.currentTimeMillis();
			List<MainCliente> threadList = new ArrayList<>();
			for(int i=0; i < nIteraciones; i++) {
				MainCliente cl = new MainCliente();
				threadList.add(cl);
				cl.start();
				sleep(rampUp);
			}

			for(MainCliente thread : threadList) {
				thread.join();
			}
			long end = System.currentTimeMillis();
			
			long keyCreationTime = 0, updateTime = 0;
			int numKeyTimes = 0, numUpdateTimes = 0, failedRequests = 0;
			for(MainCliente cliente : threadList) {
				if(cliente.t1 > 0) {
					keyCreationTime += cliente.t1;
					numKeyTimes++;
				}
				if(cliente.t2 > 0) {
					updateTime += cliente.t2;
					numUpdateTimes++;
				}
				if(cliente.error) {
					failedRequests++;
				}
			}
			keyCreationTime = keyCreationTime/numKeyTimes;
			updateTime = updateTime/numUpdateTimes;
			String type = nIteraciones + " iteraciones, ramp up de " + rampUp + " ms, con Seguridad";

			PrintWriter logger = new PrintWriter(new File(log));
			logger.print(start + "," + end + "," + type + "," + keyCreationTime + "," + updateTime + "," + failedRequests);
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		try {
			reportarEstado();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

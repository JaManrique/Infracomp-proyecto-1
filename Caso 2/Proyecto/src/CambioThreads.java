import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CambioThreads {
	
	private static final String serverIp = "localhost"; //IP de Hamachi
	private static final int NUEVO_NUMERO_THREADS = 16; 
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket(serverIp, 8080);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintWriter pw = new PrintWriter(os, true);
		
		pw.println("CAMBIO_THREADS" + " " + NUEVO_NUMERO_THREADS);
		pw.close();
		os.close();
		is.close();
		br.close();
		socket.close();
	}
}

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Queue;
public class Buffer {

	private int capacidad;
	private Queue<Mensaje> buff;
	
	public synchronized void consultar(Mensaje msg)
	{
		while(buff.size() == capacidad)
			Thread.yield();
		buff.add(msg);
		notify();
		synchronized (msg) {
			try {
				msg.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void responder()
	{
		while(buff.isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Mensaje msg = buff.remove();
		msg.setRespuesta(Integer.toString(Integer.parseInt(msg.getConsulta()) + 1));
		msg.notify();
	}
	
	public static void main(String[] args) {
		Properties p = new Properties();
		try {
			p.load(new FileReader(new File("./data/params.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int numClientes = Integer.parseInt(p.getProperty("numClientes"));
		int numServidores = Integer.parseInt(p.getProperty("numServidores"));
		int numConsultas = Integer.parseInt(p.getProperty("numConsultas"));
		int numThreads = Integer.parseInt(p.getProperty("numThreads"));
		
		Buffer buffer = new Buffer();
		
		for(int i=0; i < numServidores; i++){
			new Servidor(numThreads, buffer);
		}
		for(int i=0; i < numClientes; i++){
			new Cliente(buffer, numConsultas).start();
		}
	}
}

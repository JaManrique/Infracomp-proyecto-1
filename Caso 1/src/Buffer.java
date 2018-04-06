import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Properties;
import java.util.Queue;

public class Buffer {

	private boolean apagar;
	private int capacidad;
	private Queue<Mensaje> buff;

	//private int machete = 0;

	public Buffer(int capacidad) {
		this.capacidad = capacidad;
		buff = new ArrayDeque<>();
		apagar = false;
	}

	public void consultar(Mensaje msg) {
		synchronized (this) {
			while (buff.size() == capacidad)
				Thread.yield();
			buff.add(msg);
			this.notify();
		}
		synchronized (msg) {
			try {
				msg.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean responder() {
		synchronized (this) {
			while (buff.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(apagar)
					return false;
			}
			Mensaje msg = buff.remove();
			msg.setRespuesta(Integer.toString(Integer.parseInt(msg.getConsulta()) + 1));
			//System.out.println("Contador respuestas: " + ++machete);
			synchronized (msg) {
				msg.notify();
			}
			return true;
		}
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
		int capacidad = Integer.parseInt(p.getProperty("capacidad"));

		Buffer buffer = new Buffer(capacidad);
		Servidor[] servidores = new Servidor[numServidores]; 
		Object fin = new Object();

		for (int i = 0; i < numServidores; i++) {
			servidores[i] = new Servidor(numThreads, buffer);
		}
		for (int i = 0; i < numClientes; i++) {
			new Cliente(buffer, numConsultas, fin).start();
		}
		
		int clientesTerminados = 0;
		while(clientesTerminados++ < numClientes){
			try {
				synchronized (fin) {					
					fin.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		buffer.apagar = true;
		synchronized (buffer) {
			buffer.notifyAll();				
		}
	}
}

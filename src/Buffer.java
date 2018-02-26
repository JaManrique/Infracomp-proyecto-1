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
	
	public synchronized Mensaje responder()
	{
		
	}
	
	public static void main(String[] args) {
		
	}
}

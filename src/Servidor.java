import java.util.List;

public class Servidor implements Runnable{

	private Thread[] threads;
	private Buffer buffer;
	private boolean termino;
	
	public Servidor(int numThreads, Buffer buffer) {
		this.buffer = buffer;
		threads = new Thread[numThreads];
		for(int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(){
				public void run (){
					buffer.responder();
				}
			};
		}
		termino = false;
	}
	
	@Override
	public void run() 
	{
		while(!termino)
		{
			for(int i = 0; i< threads.length && !termino; i++)
			{
				synchronized (buffer) {
					
				}
				threads[i].start();
				termino = false /*buffer.size == 0*/;
			}
		}
	}
}

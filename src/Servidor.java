public class Servidor implements Runnable{

	private Thread[] threads;
	private Buffer buffer;
	
	public Servidor(int numThreads, Buffer buffer) {
		this.buffer = buffer;
		threads = new Thread[numThreads];
		for(int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(this);
			threads[i].start();
		}
	}
	
	public void run (){
		while(true)
		{
			buffer.responder();
		}
	}
}

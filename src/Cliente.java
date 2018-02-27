import java.util.Random;

public class Cliente extends Thread{

	private static final int UPPER_QUERY_RANDOM_BOUND = 50;
	private static final int UPPER_MESSAGE_RANDOM_BOUND = 500;

	private int numConsultas;
	private Mensaje msg;
	private Buffer buffer;

	public Cliente(Buffer buffer) {
		Random r = new Random();
		numConsultas = r.nextInt(UPPER_QUERY_RANDOM_BOUND);
		this.buffer = buffer;
		if (this.buffer == null)
		{
			System.out.println("El buffer no debe ser nulo");
		}
	}
	
	public void run() {
		Random r = new Random();
		for(int i = 0; i < numConsultas; i++)
		{
			msg = new Mensaje(Integer.toString(r.nextInt(UPPER_MESSAGE_RANDOM_BOUND)));
			meterAlBuffer(msg);
		}
	}
	
	private void meterAlBuffer(Mensaje msg){
		buffer.consultar(msg);
	}
}

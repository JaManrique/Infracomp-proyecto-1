import java.util.Random;

public class Cliente extends Thread{
	
	private static final int UPPER_RANDOM_BOUND = 50;
	
	private int numConsultas;
	private Mensaje msg;
	private Buffer buffer;
	
	public Cliente(Buffer buffer) {
		numConsultas = (new Random()).nextInt(UPPER_RANDOM_BOUND);
		msg = new Mensaje();
		meterAlBuffer(msg);
		this.buffer = buffer;
	}
	
	private void meterAlBuffer(Mensaje msg){
		
	}
}

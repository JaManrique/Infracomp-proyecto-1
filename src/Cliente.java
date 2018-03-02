import java.util.Random;

public class Cliente extends Thread {

	private static final int UPPER_MESSAGE_RANDOM_BOUND = 500;

	private int numConsultas;
	private Mensaje msg;
	private Buffer buffer;
	private Object fin;

	public Cliente(Buffer buffer, int numConsultas, Object fin) {
		Random r = new Random();
		this.numConsultas = numConsultas;
		this.buffer = buffer;
		this.fin = fin;
		if (this.buffer == null) {
			System.out.println("El buffer no debe ser nulo");
		}
	}

	public void run() {
		Random r = new Random();
		for (int i = 0; i < numConsultas; i++) {
			msg = new Mensaje(Integer.toString(r.nextInt(UPPER_MESSAGE_RANDOM_BOUND)));
			meterAlBuffer(msg);
			//System.out.println("Mensaje resuelto: " + msg.getRespuesta());
		}
		synchronized (fin) {
			fin.notify();
		}
	}

	private void meterAlBuffer(Mensaje msg) {
		buffer.consultar(msg);
	}
}

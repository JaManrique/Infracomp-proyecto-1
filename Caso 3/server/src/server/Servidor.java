package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
	private static final int TIME_OUT = 5000;
	public static final int N_THREADS = 8;
	private static ServerSocket elSocket;
	private static Servidor elServidor;

	public Servidor() {
	}

	private static ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

	public static void main(String[] args) throws IOException {
		elServidor = new Servidor();
		elServidor.runServidor();
	}

	private void runServidor() {
		int num = 0;
		try {
			System.out.print("Puerto: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int puerto = Integer.parseInt(br.readLine());
			elSocket = new ServerSocket(puerto);
			System.out.println("Servidor escuchando en puerto: " + puerto);
			for (;;) {
				Socket sThread = null;

				sThread = elSocket.accept();
				sThread.setSoTimeout(TIME_OUT);
				System.out.println("Thread " + num + " recibe a un cliente.");
				executor.submit(new Worker(num, sThread));
				num++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void changeThreadPoolSize(int N) {
		executor =  Executors.newFixedThreadPool(N);
		
		int maxNum = 0;
		
		for(int i = 0; i < 10; i++) {
			Runnable worker = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String name = Thread.currentThread().getName();
					//System.out.println(name + " Start");
					String[] split = name.split("-");
					int numThreads = Integer.parseInt(split[3]);
					System.out.println(numThreads);
				}
			};
			
			executor.execute(worker);
		}
	}
}

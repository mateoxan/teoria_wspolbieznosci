import java.util.Random;
import java.util.concurrent.Semaphore;

abstract class MyThread extends Thread {
	protected int threads;				//iloœæ wszystkich w¹tków
	protected int thread_no;			//numer procesu
	protected Semaphores semaphores;	//semafory dla ka¿dego procesu
	protected int[] buffer;				
	protected int current = 0;			//obecnie przetwarzana komórka z bufora
	
	public MyThread(int T, int nr, Semaphores s, int[] buf){
		threads = T;
		thread_no = nr;
		semaphores = s;
		buffer = buf;
	}
	
	public void run(){
		while(true){
			semaphores.acq(thread_no);
			doWork();
			semaphores.rel((thread_no + 1) % threads);
			current = (current + 1) % buffer.length;
		}
	}
	
	public abstract void doWork();
}


class ThreadA extends MyThread{
	Random r = new Random();
	
	public ThreadA(int T, int nr, Semaphores s, int[] buf){
		super(T, nr, s, buf);
	}
	
	public void doWork(){
		buffer[current] = r.nextInt(1000) + 1;
		System.out.println("Produced item: " + buffer[current]);
	}
}

class ThreadBY extends MyThread{
	Random r = new Random();
	
	public ThreadBY(int T, int nr, Semaphores s, int[] buf){
		super(T, nr, s, buf);
	}
	
	public void doWork(){
		buffer[current] = buffer[current] + 1;
	}
}

class ThreadZ extends MyThread{
	Random r = new Random();
	
	public ThreadZ(int T, int nr, Semaphores s, int[] buf){
		super(T, nr, s, buf);
	}
	
	public void doWork(){
		System.out.println("Consumed item: " + buffer[current] + "\n");
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


class Semaphores {
	private Semaphore[] sems;
	
	public Semaphores(int T){
		sems = new Semaphore[T];
		sems[0] = new Semaphore(1);
		for(int i=1; i<T; i++)
			sems[i] = new Semaphore(0);
	}
	
	public void acq(int i){
		try {
			sems[i].acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void rel(int i){
		sems[i].release();
	}
}


public class PKstream {
	public static void main(String[] args){
		int M = 10;		//rozmiar bufora
		int T = 26;		//iloœæ w¹tków
		Semaphores s = new Semaphores(T);
		int[] buffer = new int[M];
		for(int i=0; i<M; i++)
			buffer[i] = 0;
		
		ThreadA A = new ThreadA(T, 0, s, buffer);
		ThreadBY[] BY = new ThreadBY[T-2];
		for(int i=1; i<T-1; i++)
			BY[i-1] = new ThreadBY(T, i, s, buffer);
		ThreadZ Z = new ThreadZ(T, T-1, s, buffer);
		
		A.start();
		for(int i=1; i<T-1; i++)
			BY[i-1].start();
		Z.start();
		
		try {
			A.join();
			for(int i=1; i<T; i++)
				BY[i-1].join();
			Z.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
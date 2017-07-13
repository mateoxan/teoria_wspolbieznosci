import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;


class SemBuffer {
	private int buf_size;				//rozmiar bufora
	int PKlimit;						//dozwolona iloœæ jednoczeœnie wyprodukowanych/skonsumowanych elementów
	private int itemsInBuffer = 0;		//liczba elementów w buforze
	Random r = new Random();
	
	public Semaphore semProd;
	public Semaphore semCon = new Semaphore(0);
	public Semaphore mutex = new Semaphore(1);
	
	public SemBuffer(int PKlimit) {
		this.buf_size = 2*PKlimit;
		this.PKlimit = PKlimit;
		semProd = new Semaphore(buf_size);
	}
	
	public void put(int i) throws InterruptedException {
		try {
			semProd.acquire(i);
			mutex.acquire();
			itemsInBuffer = itemsInBuffer + i;
			System.out.println(Thread.currentThread().getName()+" PRODUCED " + i + " elements. Items in buffer: " + itemsInBuffer);
			Thread.currentThread().sleep(3000);
		} finally {
			mutex.release();
			semCon.release(i);
		}
		
	}

	public void get(int i) throws InterruptedException {
		try {
			semCon.acquire(i);
			mutex.acquire();
			itemsInBuffer = itemsInBuffer - i;
			System.out.println(Thread.currentThread().getName()+" TOOK " + i + " elements. Items in buffer: " + itemsInBuffer);
			Thread.currentThread().sleep(3000);
		} finally {
			mutex.release();
			semProd.release(i);
		}
	}
}

class SemProducer extends Thread {
	private SemBuffer _buf;
	
	SemProducer(SemBuffer buffer){
		_buf = buffer;
	}
	public void run() {
		while(true){
			try {
				_buf.put(_buf.r.nextInt(_buf.PKlimit) + 1);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
}
		
class SemConsumer extends Thread {
	private SemBuffer _buf;
	
	SemConsumer(SemBuffer buffer){
		_buf = buffer;
	}
	
	public void run() {
		while(true){
			try {
				_buf.get(_buf.r.nextInt(_buf.PKlimit) + 1);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
}


public class PKrand2 {
	public static void main(String[] args) {
		int prod = 2;		//quantity of producers
		int cons = 20;		//quantity of consumers
		SemBuffer buf = new SemBuffer(10);	//w konstruktorze podajemy limit jednoczeœnie produkowanych/konsumowanych elementów
		
		ArrayList<SemProducer> producers = new ArrayList<SemProducer>(prod);
		ArrayList<SemConsumer> consumers = new ArrayList<SemConsumer>(cons);

		for (int i = 1; i <= prod; i++) {
			SemProducer p = new SemProducer(buf);
			p.setName("Producer "+i);
			producers.add(p);
		}
		for (int i = 1; i <= cons; i++) {
			SemConsumer c = new SemConsumer(buf);
			c.setName("Consumer "+i);
			consumers.add(c);
		}
		

		for (int i = 0; i < prod; i++) {
			producers.get(i).start();
		}
		for (int i = 0; i < cons; i++) {
			consumers.get(i).start();
		}

		for (int i = 0; i < prod; i++) {
			try {
				producers.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < cons; i++) {
			try {
				consumers.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

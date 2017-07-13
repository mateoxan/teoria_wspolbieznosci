import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Random;


class Buffer {
	private int buf_size;				//rozmiar bufora
	int PKlimit;						//dozwolona iloœæ jednoczeœnie wyprodukowanych/skonsumowanych elementów
	private int itemsInBuffer = 0;		//liczba elementów w buforze
	Random r = new Random();
	
	private Lock lock = new ReentrantLock();
	private Condition isNotFull = lock.newCondition();
	private Condition isNotEmpty = lock.newCondition();
	
	public Buffer(int PKlimit) {
		this.buf_size = 2*PKlimit;
		this.PKlimit = PKlimit;
	}
	
	public void put(int i) throws InterruptedException {
		lock.lock();
		try {
			while((itemsInBuffer + i) > buf_size) {
				isNotFull.await();
			}
			itemsInBuffer = itemsInBuffer + i;
			System.out.println(Thread.currentThread().getName()+" PRODUCED " + i + " elements. Items in buffer: " + itemsInBuffer);
			Thread.currentThread().sleep(5000);
		} finally {
			isNotEmpty.signal();
			lock.unlock();
		}
		
	}

	public void get(int i) throws InterruptedException {
		lock.lock();
		try {
			while(i > itemsInBuffer){
				isNotEmpty.await();
			}
			itemsInBuffer = itemsInBuffer - i;
			System.out.println(Thread.currentThread().getName()+" TOOK " + i + " elements. Items in buffer: " + itemsInBuffer);
			Thread.currentThread().sleep(5000);
		} finally {
			isNotFull.signal();
			lock.unlock();
		}
	}
}

class Producer extends Thread {
	private Buffer _buf;
	
	Producer(Buffer buffer){
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
		
class Consumer extends Thread {
	private Buffer _buf;
	
	Consumer(Buffer buffer){
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


public class PKrand {
	public static void main(String[] args) {
		int prod = 2;		//quantity of producers
		int cons = 20;		//quantity of consumers
		Buffer buf = new Buffer(10);	//w konstruktorze podajemy limit jednoczeœnie produkowanych/konsumowanych elementów
		
		ArrayList<Producer> producers = new ArrayList<Producer>(prod);
		ArrayList<Consumer> consumers = new ArrayList<Consumer>(cons);

		for (int i = 1; i <= prod; i++) {
			Producer p = new Producer(buf);
			p.setName("Producer "+i);
			producers.add(p);
		}
		for (int i = 1; i <= cons; i++) {
			Consumer c = new Consumer(buf);
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

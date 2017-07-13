import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class SemBuffer {
	private LinkedList<Integer> buffer = new LinkedList<Integer>();
	private int producers;
	private int producers_finished = 0;
	private int producedItems = 0;
	public Semaphore semProd;
	public Semaphore semCon = new Semaphore(0);
	public Semaphore mutex = new Semaphore(1);
	
	public SemBuffer(int lim, int producers) {
		this.producers = producers;
		semProd = new Semaphore(lim);
	}
	
	public void put(int i){
		try{
			mutex.acquire();
			buffer.add(i);
			System.out.println(Thread.currentThread().getName() + " PRODUCED item nr " + i + ". Items in buffer: " + buffer.size());
		} catch (InterruptedException ie) {
			System.out.println("InterruptedException zlapane w put");
		} finally {
			mutex.release();
		}
	}

	public void get() throws InterruptedException {
		int value = buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " TOOK item nr " + value + ". Items in buffer: " + buffer.size());
	}
	
	public void productionFinished(){
		producers_finished++;
	}
	
	public int produceItem(){
		return ++producedItems;
	}
	
	public boolean checkItems(){		
		if(producers_finished < producers || buffer.size() > 0){
			return true;
		}
		else {
			return false;
		}
	}
}

class SemProducer extends Thread {
	private SemBuffer _buf;
	private int produced; 
	
	SemProducer(SemBuffer buffer, int qprod){
		_buf = buffer;
		produced = qprod;
	}
	public void run() {
		for (int i = 1; i <= produced; ++i) {
			try {	
				_buf.semProd.acquire();
				_buf.put(_buf.produceItem());
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			} finally {
				_buf.semCon.release();
			}
		}
		_buf.productionFinished();
	}
}
		
class SemConsumer extends Thread {
	private SemBuffer _buf;
	
	SemConsumer(SemBuffer buffer){
		_buf = buffer;
	}
	
	public void run() {
		while(true) {
			try {
				_buf.semCon.tryAcquire(1000, TimeUnit.MILLISECONDS);
				_buf.mutex.acquire();
				if(_buf.checkItems()){
					_buf.get();
					_buf.mutex.release();
					sleep(10);
				}
				else {
					_buf.mutex.release();
					break;
				}
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			} finally {
				_buf.semProd.release();
			}
		}
	}
}


public class PKmon2 {
	public static void main(String[] args) {
		int prod = 20;		//quantity of producers
		int cons = 1;		//quantity of consumers
		SemBuffer buf = new SemBuffer(10, prod);
		
		ArrayList<SemProducer> producers = new ArrayList<SemProducer>(prod);
		ArrayList<SemConsumer> consumers = new ArrayList<SemConsumer>(cons);

		for (int i = 1; i <= prod; i++) {
			SemProducer p = new SemProducer(buf, 10);
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

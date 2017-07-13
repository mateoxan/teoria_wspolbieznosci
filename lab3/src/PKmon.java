import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.ArrayList;


class Buffer {
	private int limit;
	private LinkedList<Integer> buffer = new LinkedList<Integer>();
	private int producers, consumers;
	private int producers_finished = 0;
	private int producedItems = 0;
	
	private Lock lock = new ReentrantLock();
	private Condition isNotFull = lock.newCondition();
	private Condition isNotEmpty = lock.newCondition();
	
	public Buffer(int limit, int producers, int consumers) {
		this.limit = limit;
		this.producers = producers;
		this.consumers = consumers;
	}
	
	public void put(int i) throws InterruptedException {
		lock.lock();
		try {
			while(buffer.size() == limit) {
				isNotFull.await();
			}
			
			buffer.add(i);
			System.out.println(Thread.currentThread().getName()+" PRODUCED item nr " + i + ". Items in buffer: " + buffer.size());
		} finally {
			isNotEmpty.signal();
			lock.unlock();
		}
		
	}

	public void get() throws InterruptedException {
		lock.lock();
		int value;
		try {
			while(buffer.size() == 0){
				isNotEmpty.await();
			}
		
			value = buffer.removeFirst();
			System.out.println(Thread.currentThread().getName()+" TOOK item nr " + value + ". Items in buffer: " + buffer.size());
		} finally {
			isNotFull.signal();
			lock.unlock();
		}
	}
	
	public int size() {
		return buffer.size();
	}
	
	public void productionFinished(){
		producers_finished++;
	}
	
	public int getFinishedProducers(){
		return producers_finished;
	}
	
	public int getProducers(){
		return this.producers;
	}
	
	public int getConsumers(){
		return this.consumers;
	}
	
	public int produceItem(){
		return ++producedItems;
	}
}

class Producer extends Thread {
	private Buffer _buf;
	
	Producer(Buffer buffer){
		_buf = buffer;
	}
	public void run() {
		try {
			int p;
			for (int i = 1; i <= 30; ++i) {
				p = _buf.produceItem();
				_buf.put(p);
			}
			_buf.productionFinished();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
		
class Consumer extends Thread {
	private Buffer _buf;
	
	Consumer(Buffer buffer){
		_buf = buffer;
	}
	
	public void run() {
	  try {
		  while(_buf.getFinishedProducers() < _buf.getProducers() || _buf.size() > 0) {
			  _buf.get();
			  sleep(0);
		  }
	  } catch (InterruptedException ie) {
		 ie.printStackTrace();
	  }
	}
}


public class PKmon {
	public static void main(String[] args) {
		int prod = 2;		//quantity of producers
		int cons = 20;		//quantity of consumers
		Buffer buf = new Buffer(10, prod, cons);
		
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

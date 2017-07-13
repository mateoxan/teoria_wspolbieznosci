import java.util.concurrent.atomic.AtomicInteger;

class SyncCounter {
	private int n=0;

	public synchronized void inc() {
		n++;
	}
	
	public synchronized void dec() {
		n--;
	}
	
	public void start() {
		try {
			Thread i = new Thread(new Runnable() {
				public void run() {
					for(int i=0; i<700000; i++)
						inc();
				}
			});
		
			Thread d = new Thread(new Runnable() {
				public void run() {
					for(int i=0; i<700000; i++)
						dec();
				}
			});
		
			i.start();
			d.start();
		
			i.join();
			d.join();
			
			System.out.println("Value of variable: "+n);
			
		} catch (InterruptedException ie){
		}
	}
}
	

class AtomicCounter {
	private AtomicInteger n = new AtomicInteger(0);

	public void inc() {
		n.getAndIncrement();
	}
	
	public void dec() {
		n.getAndDecrement();
	}
	
	public void start() {
		try {
			Thread i = new Thread(new Runnable() {
				public void run() {
					for(int i=0; i<700000; i++)
						inc();
				}
			});
		
			Thread d = new Thread(new Runnable() {
				public void run() {
					for(int i=0; i<700000; i++)
						dec();
				}
			});
		
			i.start();
			d.start();
		
			i.join();
			d.join();
			
			System.out.println("Value of variable: "+n);
			
		} catch (InterruptedException ie){
		}
	}	
}

public class Counter {
	public static void main(String[] args) {
		System.out.println("atomic:");
		long start=System.currentTimeMillis();
		  
		AtomicCounter c = new AtomicCounter();
		c.start();
		
		long end=System.currentTimeMillis();
		System.out.println("Time: "+(end-start)+" ms");
		
		
		System.out.println("\nsynchronized:");
		start=System.currentTimeMillis();
		  
		SyncCounter c2 = new SyncCounter();
		c2.start();
		
		end=System.currentTimeMillis();
		System.out.println("Time: "+(end-start)+" ms");

	}
}

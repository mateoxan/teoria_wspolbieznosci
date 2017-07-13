public class SyncCounter {
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

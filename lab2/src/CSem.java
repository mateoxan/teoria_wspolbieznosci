public class CSem {
	private int resources;
	private MySemaphore sem1;
	private MySemaphore sem2;
	
	public CSem() {
		this.resources = 1;
		sem1 = new MySemaphore(0);
		sem2 = new MySemaphore(1);
	}
	public CSem(int resources) {
		this.resources = resources;
		sem1 = new MySemaphore(0);
		sem2 = new MySemaphore(1);
	}
	
	public synchronized void acquire() {
		sem2.acquire();
		resources--;
		if(resources < 0) {
			sem2.release();
			sem1.acquire();
		}
		else sem2.release();
	}
	
	public synchronized void release() {
		sem2.acquire();
		resources++;
		if(resources >= 0)
			sem1.release();
		sem2.release();
	}
	
	public int resval() {
		return resources;
	}
}
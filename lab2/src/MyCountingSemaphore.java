public class MyCountingSemaphore {
	private int resources;
	
	public MyCountingSemaphore() {
		this.resources = 1;
	}
	public MyCountingSemaphore(int resources) {
		this.resources = resources;
	}
	
	public synchronized void acquire() {
		try {
			while(resources <= 0) {
				wait();
			}
			resources--;
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public synchronized void release() {
		resources++;
		notifyAll();
	}
	
	public int resval() {
		return resources;
	}
}
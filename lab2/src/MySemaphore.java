public class MySemaphore {
	private boolean locked;
	
	public MySemaphore() {
		locked = false;
	}
	public MySemaphore(int init) {
		locked = (init == 0);
	}

	public synchronized void acquire() {
		try {
			if(locked) {
				wait();
			}
			locked = true;
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public synchronized void release() {
		locked = false;
		notify();
	}
}
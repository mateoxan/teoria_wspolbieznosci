// Race2.java
// wyscig

class Counter {
  private int _val;
  public Counter(int n) {
	_val = n;
  }
  public void inc() {
	_val++;
  }
  public void dec() {
	_val--;
  }
  public int value() {
	return _val;
  }
}

class IThread extends Thread {
  private Counter _cnt;
  MySemaphore sem = null;

  public IThread(MySemaphore sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
 
  public void run() {
	for (int i = 0; i < 10000; ++i) {
		try{
			sem.acquire();  
			_cnt.inc();
			//System.out.println("INC: value = " + _cnt.value());
			sem.release();
			sleep(1);
		} catch(InterruptedException ie) { }
	}
  }
}

class DThread extends Thread {
  private Counter _cnt;
  MySemaphore sem = null;

  public DThread(MySemaphore sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
  
  public void run() {
	for (int i = 0; i < 10000; ++i) {
		try{
			sem.acquire();  
			_cnt.dec();
			//System.out.println("DEC: value = " + _cnt.value());
			sem.release();
			sleep(1);
		} catch(InterruptedException ie) { }
	}
  }
}

public class Race2 {
	public static void main(String[] args) {
		Counter cnt = new Counter(0);
		MySemaphore semaphore = new MySemaphore();
		IThread it = new IThread(semaphore, cnt);
		DThread dt = new DThread(semaphore, cnt);
	
		it.start();
		dt.start();
	
		try {
			it.join();
			dt.join();
		} catch(InterruptedException ie) { }
	
		System.out.println("\nEND! Value = " + cnt.value());
	}
}

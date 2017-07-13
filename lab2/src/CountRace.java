// Race.java
// wyscig z u¿yciem licz¹cego semafora

class IThreadC extends Thread {
  private Counter _cnt;
  MyCountingSemaphore sem = null;

  public IThreadC(MyCountingSemaphore sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
 
  public void run() {
	for (int i = 0; i < 100; ++i) {
		try{
			sem.acquire();  
			_cnt.inc();
			//System.out.println(Thread.currentThread().getName() + " - INC: value = " + _cnt.value() + "  resources: " + sem.resval());
			sem.release();
			sleep(10);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
  }
}

class DThreadC extends Thread {
  private Counter _cnt;
  MyCountingSemaphore sem = null;

  public DThreadC(MyCountingSemaphore sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
  
  public void run() {
	for (int i = 0; i < 100; ++i) {
		try{
			sem.acquire();  
			_cnt.dec();
			//System.out.println(Thread.currentThread().getName() + " - DEC: value = " + _cnt.value() + "  resources: " + sem.resval());
			sem.release();
			sleep(10);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
  }
}

public class CountRace {
	public static void main(String[] args) {
		Counter cnt = new Counter(0);
		MyCountingSemaphore semaphore = new MyCountingSemaphore(3);
		IThreadC it = new IThreadC(semaphore, cnt);
		DThreadC dt = new DThreadC(semaphore, cnt);
		IThreadC it2 = new IThreadC(semaphore, cnt);
		DThreadC dt2 = new DThreadC(semaphore, cnt);
		
	
		it.start();
		dt.start();
		it2.start();
		dt2.start();
	
		try {
			it.join();
			dt.join();
			it2.join();
			dt2.join();
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	
		System.out.println("Value = " + cnt.value());
	}
}

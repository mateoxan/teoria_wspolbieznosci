// Race.java
// wyscig z u¿yciem licz¹cego semafora

class IThreadC2 extends Thread {
  private Counter _cnt;
  CSem sem = null;

  public IThreadC2(CSem sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
 
  public void run() {
	for (int i = 0; i < 1000; ++i) {
		try{
			sem.acquire();  
			_cnt.inc();
			//System.out.println(Thread.currentThread().getName() + " - INC: value = " + _cnt.value() + "  resources: " + sem.resval());
			sem.release();
			sleep(1);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
  }
}

class DThreadC2 extends Thread {
  private Counter _cnt;
  CSem sem = null;

  public DThreadC2(CSem sem, Counter c){
    _cnt = c;
    this.sem = sem;
  }
  
  public void run() {
	for (int i = 0; i < 1000; ++i) {
		try{
			sem.acquire();  
			_cnt.dec();
			//System.out.println(Thread.currentThread().getName() + " - DEC: value = " + _cnt.value() + "  resources: " + sem.resval());
			sem.release();
			sleep(1);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
  }
}

public class Race3 {
	public static void main(String[] args) {
		Counter cnt = new Counter(0);
		CSem semaphore = new CSem(3);
		IThreadC2 it = new IThreadC2(semaphore, cnt);
		DThreadC2 dt = new DThreadC2(semaphore, cnt);
		IThreadC2 it2 = new IThreadC2(semaphore, cnt);
		DThreadC2 dt2 = new DThreadC2(semaphore, cnt);
		
	
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

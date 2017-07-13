import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Fork {
	private int id;
	private Semaphore s;
	
	public Fork (int id) {
		this.id = id;
		this.s = new Semaphore(1);
	}
	
	public boolean pickUp() {
		try {
			if(s.tryAcquire(200, TimeUnit.MILLISECONDS)) {
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void putDown() {
		s.release();
	}
	
	public int forkID() {
		return this.id;
	}
}

class Philosopher extends Thread {
	private int id;
	private boolean hungry;
	private Fork lowerFork;
	private Fork higherFork;
	
	public Philosopher(int id, Table t) {
		this.id = id;
		hungry = true;
		if(id==1){
			lowerFork = t.forks[0];
			higherFork = t.forks[t.getPhilsCount()-1];
		} else {
			lowerFork = t.forks[id-2];
			higherFork = t.forks[id-1];
		}
	}
	
	public void run() {
		try {
			while(true) {
				if(hungry) {
					if(lowerFork.pickUp()) {
						System.out.println(id + " picked up fork nr " + lowerFork.forkID());
						if(higherFork.pickUp()) {
							System.out.println(id + " picked up fork nr " + higherFork.forkID());
							eat();
							hungry = false;
							higherFork.putDown();
							System.out.println(id + " put down fork nr " + higherFork.forkID());
						}
						lowerFork.putDown();
						System.out.println(id + " put down fork nr " + lowerFork.forkID());
					}
				} else {
					think();
					hungry = true;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
			

	
	private void eat() throws InterruptedException{
		System.out.println(id + " is eating");
		Thread.sleep(2000);
	}
	
	private void think() throws InterruptedException {
		System.out.println(id + " is thinking\n");
		Thread.sleep(5000);
	}
	
	public int philID() {
		return id;
	}
}

class Table {
	Fork[] forks;
	private Philosopher[] phils;
	private int philosophersCount;
	
	public Table (int count) {
		philosophersCount = count;
		forks = new Fork[count];
		phils = new Philosopher[count];
		for(int i=0; i<count; i++) {
			forks[i] = new Fork(i+1);
		}
		for(int i=0; i<count; i++) {
			phils[i] = new Philosopher(i+1, this);
		}
	}
	
	public void work() {
		for(int i=0; i<philosophersCount; i++) {
			phils[i].start();
		}
		
		try {
			for(int i=0; i<philosophersCount; i++) {
				phils[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getPhilsCount() {
		return philosophersCount;
	}
}

public class DiningPhilosophers {
	public static void main(String[] args){
		int N = 5;		//number of philosophers
		Table t = new Table(N);
		t.work();
	}
}
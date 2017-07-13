import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fork2 {
	private int id;
	private boolean onTable;
	
	public Fork2 (int id) {
		this.id = id;
		this.onTable = true;
	}
	
	public void pickUp() {
		onTable = false;
	}
	
	public void putDown() {
		onTable = true;
	}
	
	public int forkID() {
		return this.id;
	}
	
	public boolean available() {
		if(onTable) 
			return true;
		else
			return false;
	}
}

class Philosopher2 extends Thread {
	private int id;
	private boolean hungry;
	private Fork2 rightFork;
	private Fork2 leftFork;
	private Table2 t;
	
	public Philosopher2(int id, Table2 t) {
		this.id = id;
		hungry = true;
		this.t = t;
		if(id==1){
			rightFork = t.forks[t.getPhilsCount()-1];
			leftFork = t.forks[0];
		} else {
			rightFork = t.forks[id-2];
			leftFork = t.forks[id-1];
		}
	}
	
	public void run() {
		try {
			while(true) {
				if(hungry) {
					t.waiter.lock();
						if(rightFork.available() && leftFork.available()) {
							rightFork.pickUp();
							System.out.println(id + " picked up fork nr " + rightFork.forkID());
							leftFork.pickUp();
							System.out.println(id + " picked up fork nr " + leftFork.forkID());
							t.waiter.unlock();
							eat();
							hungry = false;
							rightFork.putDown();
							System.out.println(id + " put down fork nr " + rightFork.forkID());
							leftFork.putDown();
							System.out.println(id + " put down fork nr " + leftFork.forkID() + "\n");
						} else {
							t.waiter.unlock();
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
		System.out.println("\n" + id + " is thinking\n");
		Thread.sleep(5000);
	}
	
	public int philID() {
		return id;
	}
}

class Table2 {
	Fork2[] forks;
	private Philosopher2[] phils;
	private int philosophersCount;
	Lock waiter;
	
	public Table2 (int count) {
		philosophersCount = count;
		waiter = new ReentrantLock();
		forks = new Fork2[count];
		phils = new Philosopher2[count];
		for(int i=0; i<count; i++) {
			forks[i] = new Fork2(i+1);
		}
		for(int i=0; i<count; i++) {
			phils[i] = new Philosopher2(i+1, this);
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

public class DiningPhilosophers2 {
	public static void main(String[] args){
		int N = 5;		//number of philosophers
		Table2 t = new Table2(N);
		t.work();
	}
}
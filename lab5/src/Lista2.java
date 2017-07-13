import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lista2 {
	private Object val;
	private Lista2 next;
	private Lock lock = new ReentrantLock();
	private static long sleepTime;
	
	public Lista2(Object o, Lista2 next) {
		this.val = o;
		this.next = next;
	}
	
	public boolean contains(Object o) throws InterruptedException {
		Lista2 current = this;
		lock.lock();
		try {
			while(current != null) {
				if(current.val == o) {
					Thread.sleep(sleepTime);
					return true;
				}
				current = current.next;
			}
		} finally {
			lock.unlock();
		}
		return false;
	}
	
	public void remove(Object o) throws InterruptedException {
		Lista2 current = this;
		Lista2 prev = null;
		lock.lock();
		try {
			while(current != null) {
				if(current.val == o){
					if(prev != null) {
						prev.next = current.next;
						current.next = null;
					}
					Thread.sleep(sleepTime);
					return;
				}
				prev = current;
				current = current.next;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void add(Object o) throws InterruptedException {
		if(o == null)
			return;
		Lista2 current = this;
		Lista2 prev = null;
		lock.lock();
		try {
			while(current != null) {
				prev = current;
				current = current.next;
				if(current == null) {
					prev.next = new Lista2(o, null);
					Thread.sleep(sleepTime);
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	
	public void printlist() {
		Lista2 current = this;
		while(current != null) {
			System.out.print(current.val + "   ");
			current = current.next;
		}
		System.out.println();
	}
	
	static class MyThread extends Thread {
		private Object[] o;
		private Lista2 lista;
		
		public MyThread(Object[] o, Lista2 lista) {
			super();
			this.o = o;
			this.lista = lista;
		}
		
		public void run() {
			for (int i = 0, n = o.length; i < 10; ++i) {
				try {
					lista.add(o[i % n]);
					lista.contains(o[(i + 1) % n]);
					lista.remove(o[(i + 2) % n]);
					lista.contains(o[(i + 3) % n]);
					lista.add(o[(i + 4) % n]);
					lista.remove(o[(i + 5) % n]);
					lista.add(o[(i + 6) % n]);
					lista.remove(o[(i + 7) % n]);
					lista.contains(o[(i + 8) % n]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

		
	public static void main(String[] args) {
		Object[] o = {"Polska", 7.0, "Niemcy", 21, 87.0, "kot ma ale", 33, "banan"};
		Lista2 lista = new Lista2("ala ma kota", null);
		
		for (sleepTime = 0; sleepTime <= 100; sleepTime += 10) {
			long time = System.nanoTime();
			Thread[] t = {new MyThread(o, lista), new MyThread(o, lista), new MyThread(o, lista)};
			for(int i = 0; i < t.length; ++i) {
				t[i].start();
			}
			for(int i = 0; i < t.length; ++i) {
				try {
					t[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			time = System.nanoTime() - time;
			System.out.println(sleepTime + " " + time);
		}
		
		/*for(int i=0; i<o.length; i++)
			try {
				lista.add(o[i]);
				lista.printlist();
				if(lista.contains(o[6]))
					System.out.println("TRUE");
				else
					System.out.println("FALSE");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		
		try {
			lista.remove("ola ma kota");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lista.printlist();*/
	}
}
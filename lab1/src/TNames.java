class MyThread extends Thread {
	public void run() {
		System.out.println("Thread 1 name: "+Thread.currentThread().getName());
	}
}

class MyRunnable implements Runnable {
	public void run() {
		System.out.println("Thread 2 name: "+Thread.currentThread().getName());
	}
}


public class TNames {
	public static void main(String[] args) {
		try {
			MyThread t1 = new MyThread();
			Thread t2 = new Thread(new MyRunnable());
			
			t1.start();
			t2.start();
		
			t2.join();
			t1.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
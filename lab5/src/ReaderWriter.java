import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Reader extends Thread {
	private int nr;
	private ReadingRoom room;
	
	public Reader (int number, ReadingRoom r) {
		nr = number;
		room = r;
	}
	
	public void run() {
		//System.out.println("Reader nr " + nr + " is waiting for reading.");
		room.start_reading();
		//System.out.println("Reader nr " + nr + " is reading.");
		room.finish_reading();
		//System.out.println("Reader nr " + nr + " finished reading.");
	}
}

class Writer extends Thread {
	private int nr;
	private ReadingRoom room;
	
	public Writer (int number, ReadingRoom r) {
		nr = number;
		room = r;
	}
	
	public void run() {
		//System.out.println("Writer nr " + nr + " is waiting for writing.");
		room.start_writing();
		//System.out.println("Writer nr " + nr + " is writing.");
		room.finish_writing();
		//System.out.println("Writer nr " + nr + " finished writing.");
	}
}

class ReadingRoom {
	private Lock r_room = new ReentrantLock();
	private Condition readers = r_room.newCondition();
	private Condition writers = r_room.newCondition();
	private int waiting_readers = 0;
	private int waiting_writers = 0;
	private int writing = 0;
	private int reading = 0;
	private ReaderWriter rw;
	
	public ReadingRoom(ReaderWriter rw){
		this.rw = rw;
	}
	
	public void start_reading(){
		long start = System.nanoTime();
		r_room.lock();
		try {
			while (waiting_writers > 0 || writing > 0) {
				waiting_readers++;
				readers.await();
			}
			rw.waitForReadingTime += (System.nanoTime() - start);
			reading++;
		} catch (InterruptedException e) {
		} finally {
			r_room.unlock();
		}
	}
	
	
	public void finish_reading(){
		//long start = System.nanoTime();
		r_room.lock();
		//rw.waitForReadingTime += System.nanoTime() - start;
		try {
			reading--;
			if (reading == 0) {
				if (waiting_writers > 0) {
					waiting_writers--;
				}
				writers.signal();
			}
		} finally {
			r_room.unlock();
		}
	}
	
	public void start_writing(){
		long start = System.nanoTime();
		r_room.lock();
		try {
			while (reading + writing > 0) {
				waiting_writers++;
				writers.await();
			}
			rw.waitForWritingTime += (System.nanoTime() - start);
			writing = 1;
		} catch (InterruptedException e) {
		} finally {
			r_room.unlock();
		}
	}
	
	public void finish_writing() {
		//long start = System.nanoTime();
		r_room.lock();
		//rw.waitForWritingTime += System.nanoTime() - start;
		try {
			writing = 0;
			if (waiting_readers == 0) {
				if (waiting_writers > 0) {
					waiting_writers--;
				}
				writers.signal();
			}
			else {
				waiting_readers = 0;
				readers.signalAll();
			}
		} finally {
			r_room.unlock();
		}
	}
}

public class ReaderWriter{
	public long waitForReadingTime = 0;
	public long waitForWritingTime = 0;
	private int readers_count;
	private int writers_count;
	
	
	public ReaderWriter(int readers, int writers) {
		this.readers_count = readers;
		this.writers_count = writers;
	}
	
	public void doWork() {
		int i;
		ReadingRoom readingroom = new ReadingRoom(this);
		Reader[] r = new Reader[readers_count];
		Writer[] w = new Writer[writers_count];
		
		for (i = 1; i <= readers_count; i++) {
			r[i-1] = new Reader(i, readingroom);
			r[i-1].start();
		}
		for (i = 1; i <= writers_count; i++) {
			w[i-1] = new Writer(i, readingroom);
			w[i-1].start();
		}
		
		for (i = 0; i < readers_count; i++) {
			try {
				r[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (i = 0; i < writers_count; i++) {
			try {
				w[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println(String.format("%d\t%d\t%d\t%d",
		//		readers_count, writers_count, waitForReadingTime/readers_count, waitForWritingTime/writers_count));
		System.out.println(String.format("%d     %d     %d     %d",
				writers_count, readers_count, waitForReadingTime/readers_count, waitForWritingTime/writers_count));
	}
	
	public static void main(String[] args) {
		ReaderWriter rw;
				
		for (int w = 1; w <= 10; w++) {
			for (int r = 10; r <= 100; r += 10) {
				rw = new ReaderWriter(r, w);
				rw.doWork();
			}
			System.out.println();
		}
		System.out.println("(writers, readers, avg wait for reading, avg wait for writing)");
	}
}
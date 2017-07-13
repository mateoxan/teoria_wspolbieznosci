import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class Cache {
	private ConcurrentHashMap<Double, Double> cache = new ConcurrentHashMap<Double, Double>();
	Random r = new Random();

	public Double computeSqrt(double key) {
		 Double sqrt = cache.get(key);
	     if(sqrt == null) {
	         sqrt = Math.sqrt(key);
	         Double existing = cache.putIfAbsent(key, sqrt);
	         if(existing != null) {		//jeœli w cache jest ju¿ wartoœæ dla danego klucza (inny w¹tek wstawi³ j¹ w miêdzyczasie)
	             sqrt = existing;
	         }
	     }
	     return sqrt;
	}
	
	public void clearCache(){
		cache.clear();
	}
}

class MyThread extends Thread {
	private Cache c;
	
	MyThread (Cache cache){
		this.c = cache;
	}
	
	public void run() {
		int x;
		for(int i=0; i<1000; i++){
			x = c.r.nextInt(101);
			c.computeSqrt(x);
		}
	}
}

public class MainCache {
	public static void main(String[] args){
		Cache cache = new Cache();
		int threads; 
		long start, time;
		
		for(threads=1000; threads<=10000; threads+=1000){
			ArrayList<MyThread> list = new ArrayList<MyThread>(threads);
			for(int i = 1; i <= threads; i++) {
				MyThread mt = new MyThread(cache);
				mt.setName("Thread "+i);
				list.add(mt);
			}
			start = System.currentTimeMillis();
			for(int i = 0; i < threads; i++) {
				list.get(i).start();
			}
		
			for(int i = 0; i < threads; i++) {
				try {
					list.get(i).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			time = System.currentTimeMillis() - start;
			System.out.println("Threads: "+threads+", Time: "+time);
			cache.clearCache();
		}
	}
}
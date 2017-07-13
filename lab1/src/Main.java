
public class Main {

	public static void main(String[] args) {
		System.out.println("Without 'synchronized':");
		long start=System.currentTimeMillis();
		  
		Counter c = new Counter();
		c.start();
		
		long end=System.currentTimeMillis();
		System.out.println("Time: "+(end-start)+" ms");
		
		
		System.out.println("\nWith 'synchronized':");
		start=System.currentTimeMillis();
		  
		SyncCounter c2 = new SyncCounter();
		c2.start();
		
		end=System.currentTimeMillis();
		System.out.println("Time: "+(end-start)+" ms");

	}

}

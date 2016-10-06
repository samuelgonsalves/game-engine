package section2;
public class ForkExample implements Runnable {

	int i; // the ID of the thread, so we can control behavior
	boolean busy; // the flag, Thread 1 will wait until Thread 0 is no longer busy before continuing
	ForkExample other; // reference to the other thread we will synchronize on. This is needed so we can control behavior.
	static int count;
	// create the runnable object
	public ForkExample(int i, ForkExample other) {
		this.i = i; // set the thread ID (0 or 1)
		if(i==0) { busy = true; } // set the busy flag so Thread 1 waits for Thread 0
		else { this.other = other; }
	}
	public synchronized void increment_count()
	{
		count++;
		System.out.println("In thread "+i+" Count:"+count);
	}
	// synchronized method to test if thread is busy or not
	public synchronized boolean isBusy() { return busy; }  

	public void run() {
		increment_count();
		if(i==0) { // 1st thread, sleep for a while, then notify threads waiting
			try {
				Thread.sleep(4000);
				synchronized(this) {
					notify();
					notify();
				}
				Thread.sleep(4000); 
				synchronized(this) {
					busy = false; // must synchronize while editing the flag
					notify();
					notify();
				}
			}
			catch(InterruptedException tie) { tie.printStackTrace(); }
		}
		
		else {
			while(other.isBusy()) { // check if other thread is still working
				System.out.println("Waiting in Thread "+i);
				// must synchronize to wait on other object
				try { synchronized(other) { other.wait(); } } // note we have synchronized on the object we are going to wait on
				catch(InterruptedException tie) { tie.printStackTrace(); }
			}
			System.out.println("Thread "+i+" has finished!");
		}
	}

	public static void main(String[] args) {
		ForkExample t1 = new ForkExample(0, null); 
		ForkExample t2 = new ForkExample(1, t1);
		ForkExample t3 = new ForkExample(2, t1);
		Thread x=new Thread(t2);
		x.start();
		Thread y=new Thread(t1);
		y.start();
		Thread z=new Thread(t3);
		z.start();
		try {
			//To get the final count, we wait for all 3 threads to terminate
			x.join();
			y.join();
			z.join();
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		
		
		System.out.println("Final Count: "+ForkExample.count);
		
	}

}
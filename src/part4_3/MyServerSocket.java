package part4_3;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MyServerSocket implements Runnable 
{
	static ConcurrentHashMap<Integer, OutputStream> hm_output=new ConcurrentHashMap<>();
	static ConcurrentHashMap<Integer, InputStream> hm_input=new ConcurrentHashMap<>();
	static int port=12000;
	static ServerSocket s=null;
	static int client_id=0;

	public static synchronized void increment_client()
	{
		client_id++;
	}
	public static synchronized int get_client()
	{
		return client_id;
	}
	
	
	
	
	public static void main(String[] args) {
		Rect rect=new Rect(4,3,2,1);
			try {
			s=new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		new Thread(new MyServerSocket()).start();; //Accepts connections
		while(true)
		{
			
			for(int i=0;i<get_client();i++)
			{
					try {
						ObjectOutputStream out=new ObjectOutputStream(hm_output.get(i));
						ObjectInputStream in = new ObjectInputStream(hm_input.get(i));				
						Rect r=(Rect)in.readObject();
						System.out.println(r.x);
						out.writeObject(rect);
						out.reset();
						Thread.sleep(1000);
					} 
					catch (IOException | ClassNotFoundException | InterruptedException e) {
						
					}							
			}
			
		}
			
	}

	@Override
	public void run() 
	{
		while(true)
		{
			try {
				synchronized(this)
				{
					int id=get_client();
					Socket c=s.accept();
					hm_output.put(id, c.getOutputStream());
					hm_input.put(id, c.getInputStream());
					System.out.println("Connection established with client "+id);	
					increment_client();
						
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	}

}

package section4;

import java.io.*;
import java.net.*;


/*
 * Multi client, but client 1 then client 2,.. and so on..
 * Connections are accepted asynchronously
 * Communication occurs synchronously
 */
public class MyServerSocket implements Runnable 
{

	static int port=12000;
	static int num_of_clients=10;
	
	static ServerSocket s=null;
	static Socket c[]=new Socket[num_of_clients];
	static BufferedReader read[]=new BufferedReader[num_of_clients];
	
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
		
		try {
			s=new ServerSocket(port);
			new Thread(new MyServerSocket()).start();;
			
			
			String str;
			while(true)
			{
				for(int i=0;i<get_client();i++)
				{
					str=read[i].readLine();
					System.out.println("Client "+i+" says:"+str);
				}
					
			}
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true)
		{
			try {
				int id=get_client();
				c[id]=s.accept();
				read[id]=new BufferedReader(new InputStreamReader(c[id].getInputStream()));			
				System.out.println("Connection established with client "+id);	
				increment_client();
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	}

}

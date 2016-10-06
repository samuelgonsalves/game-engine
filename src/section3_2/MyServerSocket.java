package section3_2;

import java.io.*;
import java.net.*;

/*
 * 1 server, 2(fixed number) clients, Clients write, server reads
 * Both clients must be connected prior to sending data
 * first client sends first, second client sends after the first has sent
 */
public class MyServerSocket {

	static int port=12000;
	
	public static void main(String[] args) {
		ServerSocket s=null;
		int num_of_clients=2;
		try {
			s=new ServerSocket(port);
			Socket c[]=new Socket[num_of_clients];
			BufferedReader read[]=new BufferedReader[num_of_clients];
			
			for(int i=0;i<num_of_clients;i++)
			{
				c[i]=s.accept();
				System.out.println("Connection established with client "+i);
				read[i]=new BufferedReader(new InputStreamReader(c[i].getInputStream()));
			}
			
			String str;
			while(true)
			{
				for(int i=0;i<num_of_clients;i++)
				{
					str=read[i].readLine();
					System.out.println("Client "+i+" says:"+str);
				}
					
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			s.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}

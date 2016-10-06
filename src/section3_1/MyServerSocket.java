package section3_1;

import java.io.*;
import java.net.*;
/*
 * Single server, Single client, back and forth
 * Client speaks first!
 */
public class MyServerSocket 
{
	static int port=12000;
	public static void main(String[] args) 
	{
		ServerSocket s=null;
		
		try {
			s=new ServerSocket(port);
			Socket c=s.accept();
			System.out.println("Connection established!");
			String str,str1;
			BufferedReader read=new BufferedReader(new InputStreamReader(c.getInputStream()));
			PrintWriter write=new PrintWriter(c.getOutputStream(),true);
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
			while(true)
			{
					//Read from the sockets' input stream and print
					
					str1=read.readLine();
					System.out.println("Client says:"+str1);		
					System.out.print("Say something: ");
					
					//Read from console, Write to the sockets' output stream
					
					str=br.readLine();
					write.println(str);
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

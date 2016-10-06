package section3_1;
import java.io.*;
import java.net.*;


public class MySocket 
{
	static int port=12000;
	public static void main(String[] args) {
		Socket a=null;
	
		try {
			a=new Socket("localhost",port);
		
			PrintWriter write=new PrintWriter(a.getOutputStream(),true);
			BufferedReader read=new BufferedReader(new InputStreamReader(a.getInputStream()));
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String str,str1;
			
			
			while(true)
			{
					//Read from console, Write to the sockets' output stream
					System.out.print("Say something: ");
					str=br.readLine();
					write.println(str);
					
					//Read from the sockets' input stream and print
					str1=read.readLine();
					System.out.println("Server says:"+str1);
				
			}
		
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			a.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

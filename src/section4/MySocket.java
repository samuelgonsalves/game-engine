package section4;
import java.io.*;
import java.net.*;


public class MySocket 
{
	static int port=12000;
	public static void main(String[] args) 
	{
		Socket a=null;
	
		try 
		{
			a=new Socket("localhost",port);
		
			PrintWriter write=new PrintWriter(a.getOutputStream(),true);
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String str;
			
			while(true)
			{
				str=br.readLine();
				write.println(str);
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

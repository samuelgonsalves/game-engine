package part4_3;
import java.io.*;
import java.net.*;


public class MySocket
{
	static int port=12000;
	static Socket a;
	static ObjectOutputStream out = null;
	static ObjectInputStream in = null;
	
	public static void main(String[] args) 
	{
		
		Rect rect = new Rect(10, 5, 23, 1);
		try {
			a=new Socket("localhost",port);
			out=new ObjectOutputStream(a.getOutputStream());
			in=new ObjectInputStream(a.getInputStream());
			while(true)
			{
				out.writeObject(rect);
				Rect r=(Rect)in.readObject();
				System.out.println(r.x);
				out.reset();
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			
		}
		
		try {
			a.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

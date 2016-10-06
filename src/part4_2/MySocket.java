package part4_2;
import java.io.*;
import java.net.*;


public class MySocket
{
	static int port=12000;

	public static void main(String[] args) {
		Socket a=null;
		Rect rect = new Rect(10, 5, 23, 1);
		try {
			a=new Socket("localhost",port);
			ObjectOutputStream out=new ObjectOutputStream(a.getOutputStream());
			ObjectInputStream in=new ObjectInputStream(a.getInputStream());
			while(true)
			{
				out.writeObject(rect);
				Rect r=(Rect)in.readObject();
				System.out.println(r.x);
				out.reset();
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			a.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

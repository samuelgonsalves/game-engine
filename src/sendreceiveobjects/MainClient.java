package sendreceiveobjects;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainClient
{
	public static List<Myobject> mylist=new ArrayList<Myobject>();

	
	public static void main(String[] args)
	{
		Socket c;
	
		try
		{
			c = new Socket("localhost",12000);
			Myobject x=new Myobject();
			Myobject y=new Myobject();
			
			OutputStream os=c.getOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(os);
			
			mylist.add(x);
			mylist.add(y);
			oos.writeObject(mylist);
			/*
			oos.writeObject(x);
			oos.writeObject(y);
			*/
			oos.close();
			os.close();
			c.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

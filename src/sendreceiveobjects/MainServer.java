package sendreceiveobjects;
import java.io.*;
import java.net.*;
import java.util.*;



public class MainServer
{
	static ServerSocket s=null;
	public static void main(String[] args)
	{
		try
		{
			s=new ServerSocket(12000);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		(new AcceptConnections(s)).start();
	}
}
	
class AcceptConnections extends Thread
{
	private ServerSocket ss; 
	 
    //private static ServerDispatcher mServerDispatcher; 
    AcceptConnections(ServerSocket ss)
    {
    this.ss=ss;
    }
	public void run()
	{
		 while (true) 
	        {
			 try
			 {
				 Socket s=ss.accept();
				 ClientWriter cw=new ClientWriter(s);
				 cw.start();
				 ClientReader cr=new ClientReader(s);
				 cr.start();
			 }
			 catch (IOException e)
			 {
				e.printStackTrace();
			 }
			 
	        }
	}
}
 	
class ClientWriter extends Thread
{
	ObjectOutputStream oos;
	Socket s;
	ClientWriter(Socket s) throws IOException
	{
		this.s=s;	
		oos=new ObjectOutputStream(this.s.getOutputStream());
	}
	public void run()
	{
		
	}
}
class ClientReader extends Thread
{
	Socket s;
	private Client mClient; 
	ObjectInputStream ois;
	ClientReader(Socket s) throws IOException
	{
		this.s=s;
		ois=new ObjectInputStream(this.s.getInputStream());

	}
	public void run()
	{
		
	}
}
class Client
{
	 public Socket mSocket = null; 
	 public ClientReader mClientListener = null; 
	 public ClientWriter mClientSender = null; 
}
package section5;

import java.io.*;
import java.net.*;
 
/*
 * Credit for the architecture and code-
 * Website: http://inetjava.sourceforge.net/lectures/part1_sockets/InetJava-1.9-Chat-Client-Server-Example.html
 * Author: Svetlin Nakov
 * http://www.nakov.com
 */

public class MainClient
{
    public static final String SERVER_HOSTNAME = "localhost";
    public static final int SERVER_PORT = 12000;
 
    public static void main(String[] args)
    {
        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket=null;
        try {
           socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
           in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
           System.out.println("Connected to server "+ SERVER_HOSTNAME + ":" + SERVER_PORT);
        } 
        catch (IOException ioe)
        {
           System.err.println("Can not establish connection to " +SERVER_HOSTNAME + ":" + SERVER_PORT);
           ioe.printStackTrace();
           System.exit(-1);
        }
 
        Sender sender = new Sender(out);
        sender.setDaemon(true);
        sender.start();
 
        try {
        		String message;
        		while ((message=in.readLine()) != null) 
        		{
        			System.out.println(message);
        		}
        	}
        	catch (IOException ioe) 
        	{
        		System.err.println("Connection to server broken.");
        		ioe.printStackTrace();
        	}
        finally
        {
        	try
			{
				socket.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
 
    }
}
 
class Sender extends Thread
{
	private PrintWriter mOut;
	 
	public Sender(PrintWriter aOut)
	{
	        mOut = aOut;
	}
 
    public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while (!isInterrupted()) 
			{
				String message = in.readLine();
				mOut.println(message);
				mOut.flush();
			}
		} 
		catch (IOException ioe) 
		{
		}
	}
}


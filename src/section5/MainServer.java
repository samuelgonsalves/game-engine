package section5;

import java.net.*; 
import java.io.*; 
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap; 
 
/*
 * Credit for the architecture and code-
 * Website: http://inetjava.sourceforge.net/lectures/part1_sockets/InetJava-1.9-Chat-Client-Server-Example.html
 * Author: Svetlin Nakov
 * http://www.nakov.com
 */


public class MainServer 
{ 
    public static final int LISTENING_PORT = 12000; 
     
    private static ServerSocket mServerSocket; 
 
    private static ClientHandler mClientHandler; 
 
    public static void main(String[] args)
    { 
        bindServerSocket(); 

        mClientHandler = new ClientHandler(); 
        mClientHandler.start(); 
       
        new Thread(new AcceptConnections(mServerSocket, mClientHandler)).start();
    } 
 
    private static void bindServerSocket() 
    { 
        try 
        { 
            mServerSocket = new ServerSocket(LISTENING_PORT); 
            System.out.println("Server started on " + "port " + LISTENING_PORT); 
        }
        catch (IOException ioe) 
        { 
            System.err.println("Can not start listening on " + "port " + LISTENING_PORT); 
            ioe.printStackTrace(); 
            System.exit(-1); 
        } 
    } 
  
} 
 
class AcceptConnections extends Thread
{
	private static ServerSocket mServerSocket; 
	 
    private static ClientHandler mClientHandler; 
    AcceptConnections(ServerSocket ss,ClientHandler sd)
    {
    	mServerSocket=ss;
    	mClientHandler=sd;
    }
	public void run()
	{
		 while (true) 
	        { 
	            try 
	            { 
	                Socket socket = mServerSocket.accept(); 
	                Client client = new Client(); 
	                client.mSocket = socket; 
	                ClientListener clientListener = new ClientListener(client, mClientHandler); 
	                ClientSender clientSender = new ClientSender(client, mClientHandler); 
	                client.mClientListener = clientListener; 
	                clientListener.start(); 
	                client.mClientSender = clientSender; 
	                clientSender.start(); 
	                mClientHandler.addClient(client); 
	            } 
	            catch (IOException ioe)
	            { 
	                ioe.printStackTrace(); 
	            } 
	        } 
	}
}

class ClientHandler extends Thread
{ 
	
    private Vector<Client> mClients = new Vector<Client>(); 
 
    private ConcurrentHashMap<Client,String> hm1=new ConcurrentHashMap<Client,String>();//Incoming Messages

    
    /*
     *	Credit for the reverse() function-
     * 	User: Sami El-Tamawy 
     * 	Webpage: http://stackoverflow.com/questions/7569335/reverse-a-string-in-java 
     * 
     */ 
    public static synchronized String reverse(String input)
    {
        char[] in = input.toCharArray();
        int begin=0;
        int end=in.length-1;
        char temp;
        while(end>begin){
            temp = in[begin];
            in[begin]=in[end];
            in[end] = temp;
            end--;
            begin++;
        }
        return new String(in);
    }
    
    public synchronized void addClient(Client aClient) 
    { 
        mClients.add(aClient); 
    } 
 
    public synchronized void deleteClient(Client aClient)
    { 
        int clientIndex = mClients.indexOf(aClient); 
        if (clientIndex != -1) 
            mClients.removeElementAt(clientIndex); 
    } 
 
    public synchronized void dispatchMessage(Client aClient, String aMessage) 
    { 
 
        hm1.put(aClient, aMessage);
        notify(); 
    } 
 
    private synchronized String getNextMessageFromQueue() throws InterruptedException 
    {
    	String str=null;
    	String str1=null;
    	while(hm1.isEmpty())
    		wait();
    	for(int i=0;i<mClients.size();i++)
    	{
    		Client x= (Client) mClients.get(i);
    		if(x!=null)
    		{
    			str=hm1.get(x);
    			if(str!=null)
    			{
    				str1=reverse(str);
        			hm1.remove(x);	
    			}
    		}
    	}
    	return str1;
    } 
 
    private void sendMessageToAClient(Client a,String aMessage)
    {
    	a.mClientSender.sendMessage(aMessage);
    }
    
    private void broadCastMessage(Client a, String message)
    {
    	for(Client t:mClients)
    	{
    			sendMessageToAClient(t,message);
    	}
    }
    
    public void run() 
    { 
        try 
        { 
            while (true) 
            { 
            	for (int i=0; i<mClients.size(); i++) 
            	{
            	     Client client = (Client) mClients.get(i);
            	     if(hm1.get(client)!=null)
            	     {
            	    	 String msg=getNextMessageFromQueue();
            	    	 if(msg!=null)
            	    		 broadCastMessage(client, msg);
            	    		 //sendMessageToAClient(client,msg);
            	     }
            	}
            } 
        }
        catch (InterruptedException ie) { 
        } 
    } 
} 
 
 
class Client 
{ 
    public Socket mSocket = null; 
    public ClientListener mClientListener = null; 
    public ClientSender mClientSender = null; 
} 
 

class ClientListener extends Thread 
{ 
    private ClientHandler mClientHandler; 
    private Client mClient; 
    private BufferedReader mSocketReader; 
    
    public ClientListener(Client aClient, ClientHandler aClientHandler) throws IOException 
    { 
        mClient = aClient; 
        mClientHandler = aClientHandler; 
        Socket socket = aClient.mSocket; 
        mSocketReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
    } 
 

    public void run() 
    { 
        try 
        { 
            while (!isInterrupted()) 
            { 
                try 
                { 
                    String message = mSocketReader.readLine(); 
                    if (message == null) 
                        break; 
                    mClientHandler.dispatchMessage(mClient, message); 
                } 
                catch (SocketTimeoutException ste)
                { 
                } 
            } 
        } 
        catch (IOException ioex) 
        { 
        } 
 
        mClient.mClientSender.interrupt(); 
        mClientHandler.deleteClient(mClient); 
    } 
} 
 
class ClientSender extends Thread 
{ 
    private Vector<String> mMessageQueue = new Vector<String>(); 
   
    private ClientHandler mClientHandler; 
    private Client mClient; 
    private PrintWriter mOut; 
 
    public ClientSender(Client aClient, ClientHandler aClientHandler) throws IOException
    { 
        mClient = aClient; 
        mClientHandler = aClientHandler; 
        Socket socket = aClient.mSocket; 
        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()) ); 
    } 

    public synchronized void sendMessage(String aMessage) 
    { 
        mMessageQueue.add(aMessage);
        notify(); 
    } 
 

    private synchronized String getNextMessageFromQueue() throws InterruptedException 
    { 
        while (mMessageQueue.size()==0) 
            wait(); 
        String message = (String) mMessageQueue.get(0); 
        mMessageQueue.removeElementAt(0); 
        return message; 
    } 
 
    
    private void sendMessageToClient(String aMessage)
    { 
        mOut.println("Reversed String: "+aMessage); 
        mOut.flush(); 
    } 
 
    public void run() 
    { 
        try 
        { 
            while (!isInterrupted()) 
            { 
                String message = getNextMessageFromQueue(); 
                sendMessageToClient(message); 
            } 
        } 
        catch (Exception e) 
        { 
        } 
 
        mClient.mClientListener.interrupt(); 
        mClientHandler.deleteClient(mClient); 
    } 
}
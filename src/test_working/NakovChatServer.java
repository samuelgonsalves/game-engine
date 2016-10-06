package test_working;

import java.net.*; 
import java.io.*; 
import java.util.Vector; 
 
/** 
 * NakovChatServer class is the entry point for the server. 
 * It opens a server socket, starts the dispatcher thread and 
 * infinitely accepts client connections, creates threads for 
 * handling them and starts these threads. 
 */ 
public class NakovChatServer 
{ 
    public static final int LISTENING_PORT = 2002; 
    public static String KEEP_ALIVE_MESSAGE = "!keep-alive"; 
    public static int CLIENT_READ_TIMEOUT = 5*60*1000; 
    
    private static ServerSocket mServerSocket; 
 
    private static ServerDispatcher mServerDispatcher; 
 
    public static void main(String[] args)
    { 
        // Start listening on the server socket 
        bindServerSocket(); 
 
        // Start the ServerDispatcher thread 
        mServerDispatcher = new ServerDispatcher(); 
        mServerDispatcher.start(); 
 
        //accept and handle client connections 
       
        new Thread(new AcceptConnections(mServerSocket, mServerDispatcher)).start();
        
        //System.out.println("Hi!");
    } 
 
    private static void bindServerSocket() 
    { 
        try 
        { 
            mServerSocket = new ServerSocket(LISTENING_PORT); 
            System.out.println("NakovChatServer started on " + 
                "port " + LISTENING_PORT); 
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
	 
    private static ServerDispatcher mServerDispatcher; 
    AcceptConnections(ServerSocket ss,ServerDispatcher sd)
    {
    	mServerSocket=ss;
    	mServerDispatcher=sd;
    }
	public void run()
	{
		 while (true) 
	        { 
	            try { 
	                Socket socket = mServerSocket.accept(); 
	                Client client = new Client(); 
	                client.mSocket = socket; 
	                ClientListener clientListener = new ClientListener(client, mServerDispatcher); 
	                ClientSender clientSender = new ClientSender(client, mServerDispatcher); 
	                client.mClientListener = clientListener; 
	                clientListener.start(); 
	                client.mClientSender = clientSender; 
	                clientSender.start(); 
	                mServerDispatcher.addClient(client); 
	            } catch (IOException ioe) { 
	                ioe.printStackTrace(); 
	            } 
	        } 
	}
}
 
/** 
 * ServerDispatcher class is purposed to listen for messages 
 * received from the clients and to dispatch them to all the 
 * clients connected to the chat server. 
 */ 
class ServerDispatcher extends Thread
{ 
	
    private Vector mMessageQueue = new Vector(); 
    private Vector mClients = new Vector(); 
 
    /** 
     * Adds given client to the server's client list. 
     */ 
    public synchronized void addClient(Client aClient) 
    { 
        mClients.add(aClient); 
    } 
 
    /** 
     * Deletes given client from the server's client list if 
     * the client is in the list. 
     */ 
    public synchronized void deleteClient(Client aClient)
    { 
        int clientIndex = mClients.indexOf(aClient); 
        if (clientIndex != -1) 
            mClients.removeElementAt(clientIndex); 
    } 
 
    /** 
     * Adds given message to the dispatcher's message queue and 
     * notifies this thread to wake up the message queue reader 
     * (getNextMessageFromQueue method). dispatchMessage method 
     * is called by other threads (ClientListener) when a 
     * message is arrived. 
     */ 
    public synchronized void dispatchMessage(Client aClient, String aMessage) 
    { 
        Socket socket = aClient.mSocket; 
        String senderIP = 
            socket.getInetAddress().getHostAddress(); 
        String senderPort = "" + socket.getPort(); 
        aMessage = senderIP + ":" + senderPort + 
            " : " + aMessage; 
        mMessageQueue.add(aMessage); 
        notify(); 
    } 
 
    /** 
     * @return and deletes the next message from the message 
     * queue. If there is no messages in the queue, falls in 
     * sleep until notified by dispatchMessage method. 
     */ 
    private synchronized String getNextMessageFromQueue() 
    throws InterruptedException { 
        while (mMessageQueue.size()==0) 
            wait(); 
        String message = (String) mMessageQueue.get(0); 
        mMessageQueue.removeElementAt(0); 
        return message; 
    } 
 
    /** 
     * Sends given message to all clients in the client list. 
     * Actually the message is added to the client sender 
     * thread's message queue and this client sender thread 
     * is notified to process it. 
     */ 
    private void sendMessageToAllClients( 
            String aMessage) { 
        for (int i=0; i<mClients.size(); i++) { 
            Client client = (Client) mClients.get(i); 
            client.mClientSender.sendMessage(aMessage); 
        } 
    } 
 
    /** 
     * Infinitely reads messages from the queue and dispatches 
     * them to all clients connected to the server. 
     */ 
    public void run() { 
        try { 
            while (true) { 
                String message = getNextMessageFromQueue(); 
                sendMessageToAllClients(message); 
            } 
        } catch (InterruptedException ie) { 
            // Thread interrupted. Stop its execution 
        } 
    } 
} 
 
 
/** 
 * Client class contains information about a client, 
 * connected to the server. 
 */ 
class Client { 
    public Socket mSocket = null; 
    public ClientListener mClientListener = null; 
    public ClientSender mClientSender = null; 
} 
 
 
/** 
 * ClientListener class listens for client messages and 
 * forwards them to ServerDispatcher. 
 */ 
class ClientListener extends Thread { 
    private ServerDispatcher mServerDispatcher; 
    private Client mClient; 
    private BufferedReader mSocketReader; 
 //private ObjectInputStream
    
    public ClientListener(Client aClient, ServerDispatcher 
            aSrvDispatcher) throws IOException { 
        mClient = aClient; 
        mServerDispatcher = aSrvDispatcher; 
        Socket socket = aClient.mSocket; 
        socket.setSoTimeout( 
            NakovChatServer.CLIENT_READ_TIMEOUT); 
        mSocketReader = new BufferedReader( 
            new InputStreamReader(socket.getInputStream()) ); 
    } 
 
    /** 
     * Until interrupted, reads messages from the client 
     * socket, forwards them to the server dispatcher's 
     * queue and notifies the server dispatcher. 
     */ 
    public void run() { 
        try { 
            while (!isInterrupted()) { 
                try { 
                    String message = mSocketReader.readLine(); 
                    if (message == null) 
                        break; 
                    mServerDispatcher.dispatchMessage( 
                        mClient, message); 
                } catch (SocketTimeoutException ste) { 
                    mClient.mClientSender.sendKeepAlive(); 
                } 
            } 
        } catch (IOException ioex) { 
            // Problem reading from socket (broken connection) 
        } 
 
        // Communication is broken. Interrupt both listener and 
        // sender threads 
        mClient.mClientSender.interrupt(); 
        mServerDispatcher.deleteClient(mClient); 
    } 
} 
 
/** 
 * Sends messages to the client. Messages waiting to be sent 
 * are stored in a message queue. When the queue is empty, 
 * ClientSender falls in sleep until a new message is arrived 
 * in the queue. When the queue is not empty, ClientSender 
 * sends the messages from the queue to the client socket. 
 */ 
class ClientSender extends Thread { 
    private Vector mMessageQueue = new Vector(); 
 
    private ServerDispatcher mServerDispatcher; 
    private Client mClient; 
    private PrintWriter mOut; 
 
    public ClientSender(Client aClient, ServerDispatcher 
            aServerDispatcher) throws IOException { 
        mClient = aClient; 
        mServerDispatcher = aServerDispatcher; 
        Socket socket = aClient.mSocket; 
        mOut = new PrintWriter( 
            new OutputStreamWriter(socket.getOutputStream()) ); 
    } 
 
    /** 
     * Adds given message to the message queue and notifies 
     * this thread (actually getNextMessageFromQueue method) 
     * that a message is arrived. sendMessage is always called 
     * by other threads (ServerDispatcher). 
     */ 
    public synchronized void sendMessage(String aMessage) { 
        mMessageQueue.add(aMessage); 
        notify(); 
    } 
 
    /** 
     * Sends a keep-alive message to the client to check if 
     * it is still alive. This method is called when the client 
     * is inactive too long to prevent serving dead clients. 
     */ 
    public void sendKeepAlive() { 
        sendMessage(NakovChatServer.KEEP_ALIVE_MESSAGE); 
    } 
 
    /** 
     * @return and deletes the next message from the message 
     * queue. If the queue is empty, falls in sleep until 
     * notified for message arrival by sendMessage method. 
     */ 
    private synchronized String getNextMessageFromQueue() 
            throws InterruptedException { 
        while (mMessageQueue.size()==0) 
            wait(); 
        String message = (String) mMessageQueue.get(0); 
        mMessageQueue.removeElementAt(0); 
        return message; 
    } 
 
    /** 
     * Sends given message to the client's socket. 
     */ 
    private void sendMessageToClient(String aMessage) { 
        mOut.println(aMessage); 
        mOut.flush(); 
    } 
 
    /** 
     * Until interrupted, reads messages from the message queue 
     * and sends them to the client's socket. 
     */ 
    public void run() { 
        try { 
            while (!isInterrupted()) { 
                String message = getNextMessageFromQueue(); 
                sendMessageToClient(message); 
            } 
        } catch (Exception e) { 
            // Commuication problem 
        } 
 
        // Communication is broken. Interrupt both listener 
        // and sender threads 
        mClient.mClientListener.interrupt(); 
        mServerDispatcher.deleteClient(mClient); 
    } 
}
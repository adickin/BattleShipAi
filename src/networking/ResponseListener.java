/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package networking;

//Java Imports
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
 * The main thread that is setup and receive messages from the Battleship 3D server.
 * Upon receiving a valid API message the correct system is notified.
 * 
 */
public class ResponseListener extends Thread
{
	private int serverPort_;
	
	private ServerSocketChannel socketChannel_;
    private Selector selector_;
    private InetSocketAddress isa_;
    private SocketChannel server_;
    public static boolean stopFlag_;
    
    private Network network_;
    
    /*
     * Constructor for the Listener class.  Creates the encoders and decoders
     * for received communications
     */
	public ResponseListener(int myPort, SocketChannel server, Network network) throws Exception
    {
		serverPort_ = myPort;
		server_ = server;
		network_ = network;
		socketChannel_ = null;
		selector_ = null;
		isa_ = null;
		stopFlag_ = false;
    }
	
	/*
	 * The run function for the Listener class. Starts the thread, if errors
	 * occur while setting up the Server then an error message is printed.
	 */
	 public void run()
	    {
	    	try
	    	{
	    		startListening();
	    	}
	    	catch(IOException e)
	    	{
	    		System.err.println("ERROR IN LISTENER THREAD");
	    		try
	    		{
					restartForNextGame();
				}
	    		catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    }
	 
	 /*
	  * Starts the selector which will listen on a socketChannel.  Upon receiving messages
	  * or connections from another client it will parse through the BattleShip server api
	  * determine the correct event to trigger.
	  */
	 private void startListening() throws IOException
	 {
		 selector_ = Selector.open();
		 try 
	    	{	
			 	socketChannel_ = ServerSocketChannel.open();
			 	socketChannel_.configureBlocking(false);
	        	isa_ = new InetSocketAddress(serverPort_);
	        	socketChannel_.socket().bind(isa_);
	        	socketChannel_.register(selector_, SelectionKey.OP_ACCEPT);
	        	server_.register(selector_, SelectionKey.OP_READ);
	        	
	        } 
	    	catch (IOException e) {
	            System.out.println("Error creating server channel: " + e);
	            System.exit(-1);
	        }
	    	
	    	 while (selector_.select(100) >= 0 || stopFlag_) 
	    	 {
	             // Get set of ready objects
	         	Set<SelectionKey> readyKeys = selector_.selectedKeys();
	         	Iterator<SelectionKey> Itor = readyKeys.iterator();
	             
	             // Walk through set
	             while (Itor.hasNext()) 
	             {
	             	SelectionKey key = (SelectionKey)Itor.next();
	             	
	             	Itor.remove();
	             	
	             	if(key.isReadable())
	             	{
	             		SocketChannel keyChannel = (SocketChannel)key.channel();
	             		ByteBuffer buffer = ByteBuffer.allocateDirect(500);
	             		CharBuffer charBuffer = CharBuffer.allocate(500);
	             		
	             		buffer.clear();
	             		keyChannel.read(buffer);
	             		buffer.flip();
	             		
	             		Network.decoder_S.decode(buffer, charBuffer, false);
	             		charBuffer.flip();
	             		
	             		String message = charBuffer.toString();
	             		
	             		if(message.contains(ServerApi.BATTLESHIP_SERVER_NAME))
	             		{
	             			network_.requestLogin();
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_OK_RESPONSE))
	             		{
	             			network_.parseOkResponse(message);
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_MISS_RESPONSE))
	             		{
	             			network_.requestAnotherShotAfterMiss();
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_HIT_RESPONSE))
	             		{
	             			boolean wasShipSunk = false;
	             			String shipSunk = "";
	             			if(message.contains(ServerApi.CARRIER))
	             			{
	             				wasShipSunk = true;
	             				shipSunk = ServerApi.CARRIER;
	             			}
	             			else if(message.contains(ServerApi.FRIGATE))
	             			{
	             				wasShipSunk = true;
	             				shipSunk = ServerApi.FRIGATE;
	             			}
	             			else if(message.contains(ServerApi.DESTROYER))
	             			{
	             				wasShipSunk = true;
	             				shipSunk = ServerApi.DESTROYER;
	             			}
	             			else if(message.contains(ServerApi.SUBMARINE))
	             			{
	             				wasShipSunk = true;
	             				shipSunk = ServerApi.SUBMARINE;
	             			}
	             			else if(message.contains(ServerApi.BATTLESHIP))
	             			{
	             				wasShipSunk = true;
	             				shipSunk = ServerApi.BATTLESHIP;
	             			}
	             			network_.requestAnotherShotAfterHit(wasShipSunk, shipSunk);
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_FAIL_RESPONSE))
	             		{
	             			System.out.print(message);
	             			String splitString[] = message.split("[|]");
	             			
	             			if(splitString[1].equals(ServerApi.BATTLESHIP_SERVER_MATCH_RESPONSE))
	             			{
	             				System.out.println("MATCH over exiting program.");
	             				System.exit(1);
	             			}
	             			
	             			restartForNextGame();
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_LOSE_RESPONSE))
	             		{
	             			System.out.print(message);
	             			
	             			String splitString[] = message.split("[|]");
	             			if(splitString[1].equals(ServerApi.BATTLESHIP_SERVER_MATCH_RESPONSE))
	             			{
	             				System.out.println("MATCH over exiting program.");
	             				System.exit(1);
	             			}
	             			
	             			restartForNextGame();
	             		}
	             		else if(message.contains(ServerApi.BATTLESHIP_SERVER_WIN_RESPONSE))
	             		{
	             			System.out.print(message);
	             			String splitString[] = message.split("[|]");
	             			if(splitString[1].trim().equals(ServerApi.BATTLESHIP_SERVER_MATCH_RESPONSE))
	             			{
	             				System.out.println("MATCH over exiting program.");
	             				System.exit(1);
	             			}
	             			restartForNextGame();
	             		}
	             	}
	             	
	             	else if(key.isConnectable())
	             	{
	             		SocketChannel k = (SocketChannel)key.channel();
	            		if(k.isConnectionPending())
	            		{
	            			k.finishConnect();
	            		}
	             		
	             	}
	             	else
	             	{
	             		System.err.println("Unknown ERROR Occured");
	             		System.exit(1);
	             	}

	                 
	             }//end of itor while
	         }//end of while
	 }
	 
	 /*
	  * Closes the socket channel and notifies the network to restart the 
	  * client.
	  */
	 public void restartForNextGame() throws IOException
	 {
		socketChannel_.close();
		network_.notifyProxyToRestartNetworkingAndGameLayers();
	 }
	
}

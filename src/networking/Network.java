/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package networking;

//Java Imports
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

//BattleShip Imports
import main.GameLayerAndNetworkingLayerProxy;

/**
 * Sets up the Thread that will listen to responses from the BattleShip server.
 * Also contains all the functions that will allow for message sending.
 */
public class Network 
{
	public static Charset charset_S = Charset.forName("ISO-8859-1");
    public static CharsetEncoder encoder_S;
    public static CharsetDecoder decoder_S;
    
	private SocketChannel serverChannel_;
    private InetSocketAddress serverAddress_;
    public int clientPort_;
    Thread responseListener_;
	private GameLayerAndNetworkingLayerProxy layerProxy_;
    
    public static void setupMessageEncodersAndDecoders()
    {
        encoder_S = charset_S.newEncoder();
        decoder_S = charset_S.newDecoder();
    }
	
	/*
	 * Constructs the main socket channel to send data on.  Creates and starts
	 * the response listener thread to listen to responses from the BattleShip
	 * Server.
	 */
	public Network(String host, int port) throws IOException
	{
		setupMessageEncodersAndDecoders();  
        try
        {
        	serverAddress_ = new InetSocketAddress(host, port);
        	serverChannel_ = SocketChannel.open();
        	serverChannel_.configureBlocking(false);
        	serverChannel_.connect(serverAddress_);

        	while(serverChannel_.isConnectionPending())
        		if(serverChannel_.isConnectionPending())
        		{
        			serverChannel_.finishConnect();
        		}
        	responseListener_ = new Thread(new ResponseListener(port, serverChannel_, this));
        	responseListener_.start();
        }
        catch(IOException e)
        {
        	System.err.println("Error starting network connection inside network.java");
        }
        catch(Exception e)
        {
        	System.err.println("Exception making network Thread");
        }
	}
	
	/*
	 * Sets the messaging proxy that will send messages to the game layer.
	 */
	public void setProxy(GameLayerAndNetworkingLayerProxy proxy)
	{
		layerProxy_ = proxy;
	}
	
	/*
	 * Encodes a msg to be sent.  Then sends the message.
	 */
	private void sendMsg(String msg) throws IOException 
    { 
		ByteBuffer buffer = encoder_S.encode(CharBuffer.wrap(msg));
		serverChannel_.write(buffer);
    } 
	
	/*
	 * Will send a request for login from the Battleship server.
	 */
	public void requestLogin() throws IOException
	{
		//Hardcoded Teamname and password.
		String teamName = "Ballmer's Peakin";
		String password = "TEST_GAME";

		String teamPassword = "DrinkBeer";
		
		//Uncomment when its GAMETIME
		password = teamPassword;
		
		String formattedLoginMessage = 
			ServerApi.createTwoStringRequest(teamName, password);
		 try
		 {
			sendMsg(formattedLoginMessage);
		 }
		 catch(IOException exception)
		 {
			 System.err.println("IO exception in requestLogin.");
		 }
	}
	
	/*
	 * Parses an BATTLESHIP_SERVER_OK_RESPONSE from the battle ship server.
	 * 
	 * If the OK is in response to login then send the dimensions of the gameboard to 
	 * the Game Thread.
	 * 
	 * If the OK is in response to a successful game piece setup then let the Game
	 * Thread know this as well.
	 */
	public void parseOkResponse(String message) throws IOException
	{
		message = message.trim();
		
		String[] splitMessage = message.split("[|]");
		
		if(2 == splitMessage.length)
		{
			int[] dimensions = createIntegerDimensions(splitMessage[1]);
			layerProxy_.sendDimensionsOfGameBoardToGameLayer(dimensions);
		}
		else if(1 == splitMessage.length)
		{
			//let game layer know to take a shot.
			layerProxy_.requestAnotherLocationToFireOn();
		}	
	}
	
	/*
	 * Used to stop the ResponseListener thread.  Closes open connections.
	 */
	private void stopThread() throws IOException
	{
		ResponseListener.stopFlag_ = true;
		serverChannel_.close();
	}
	
	/*
	 * Shuts down the response listener thread.
	 */
	public void shutDown() throws IOException
	{
		stopThread();
	}
	
	/*
	 * Parses the Dimensions from the BattleShip Server and converts them into
	 * an Integer array.
	 */
	private int[] createIntegerDimensions(String stringDimensions)
	{
		String[] splitDimensions = stringDimensions.split("[,]");
		int[] dimensions = new int[splitDimensions.length];
		
		for(int i = 0; i < splitDimensions.length; i++)
		{
			dimensions[i] = Integer.parseInt(splitDimensions[i]);
		}
		return dimensions;
	}
	
	/*
	 * Sends the layouts of the ships to the BattleShip Server
	 */
	public void sendShipLayouts(String layouts) throws IOException
	{
		layouts += ServerApi.CARRIAGE_FEED_AND_LINE_RETURN;
		sendMsg(layouts);
	}
	
	/*
	 * Sends a shot to the BattleShip server.
	 */
	public void sendShot(String nextShot) throws IOException
	{
		nextShot += ServerApi.CARRIAGE_FEED_AND_LINE_RETURN;
		sendMsg(nextShot);
	}
	
	/*
	 * Notifies the proxy to get another place to shoot at after a miss.
	 */
	public void requestAnotherShotAfterMiss()
	{
		layerProxy_.requestAnotherLocationToFireOn();
	}
	
	/*
	 * Notifies the proxy to request another place to fire on after a hit.
	 */
	public void requestAnotherShotAfterHit(boolean wasShipSunk, String shipSunk)
	{
		layerProxy_.requestLocationToFireOnAfterHit(wasShipSunk, shipSunk);
	}
	
	/*
	 * Notifies the Proxy layer to restart the networking and game layers.
	 */
	public void notifyProxyToRestartNetworkingAndGameLayers()
	{
		try 
		{
			layerProxy_.deleteAndRestartNetworkingLayerAndGameLayer();
		} 
		catch (IOException e) 
		{
			System.out.println("ERROR RESTRING NETWORKING AND GAME LAYER");
		}
	}
}

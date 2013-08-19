/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package main;

//Java Imports
import java.io.IOException;

//BattleShip Imports
import game.BattleShip3DAI;
import networking.Network;

/**
 * Proxy between the networking layer and game layer. Allows the networking
 * layer to notify this layer to recreate the game thread and networking threads
 * after a game has ended.
 */
public class GameLayerAndNetworkingLayerProxy
{
	BattleShip3DAI ai_;
	Network network_;
	
	/*
	 * Constructor for the proxy.
	 */
	public GameLayerAndNetworkingLayerProxy(BattleShip3DAI ai, Network network)
	{
		ai_ = ai;
		network_ = network;
	}
	
	/*
	 * sends the game board dimensions to the game layer.
	 */
	public void sendDimensionsOfGameBoardToGameLayer(int[] dimensions)
	{
		ai_.setGameBoardDimensions(dimensions);
	}
	
	/*
	 * request another shot from the game layer to fire on.
	 */
	public void requestAnotherLocationToFireOn()
	{
		ai_.requestShotAfterMiss();
	}
	
	/*
	 * request another location to fire on after hitting something. Also
	 * notifies the game layer if a certain ship was sunk.
	 */
	public void requestLocationToFireOnAfterHit(boolean wasShipSunk, String shipSunk)
	{
		ai_.requestShotAfterHit(wasShipSunk, shipSunk);
	}
	
	/*
	 * Deletes the networking layer and the game layer then rebuilds them and 
	 * restarts the game layer to play the next game.
	 */
	@SuppressWarnings("deprecation")
	public void deleteAndRestartNetworkingLayerAndGameLayer() throws IOException
	{
		//Shut down the networking layer.
		network_.shutDown();
		network_ = null;
		
		//Shut down the AI layer.
		ai_.stop();
		ai_ = null;
		
		//Restart AI layer.
		ai_ = new BattleShip3DAI();
		ai_.setProxy(this);
		ai_.start();
		
		//RestartNetworkingLayer.
		network_ = new Network(BattleShipMain.serverName_S, BattleShipMain.serverPort_S);
		network_.setProxy(this);
	}
	
	/*
	 * Sends a shot to the networking layer.
	 */
	public void sendShotToNetworkingLayer(String nextShot)
	{
		try
		{
			network_.sendShot(nextShot);
		}
		catch(IOException e)
		{
			System.out.println("Error sending Shot");
		}
	}
	
	/*
	 * Sends the ship layouts to the networking layer.
	 */
	public void sendShipLayoutsToNetworkingLayer(String shipLayouts)
	{
 		try 
 		{
			network_.sendShipLayouts(shipLayouts);
		} 
 		catch (IOException e) 
 		{
			System.err.println("Ship Dimensions were not sent sucessfully");
		}
	}
}

/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package main;


//BattleShip Imports
import networking.Network;
import game.BattleShip3DAI;

/*
 * This class will handle starting of all the threads required to play the Battleship 3D game.
 * This class will also act as a create a proxy class to pass messages back and forth between the
 * networking layer and the game layer.
 */
public class BattleShipMain 
{
	private Network network_;
	private BattleShip3DAI ai_;
	private GameLayerAndNetworkingLayerProxy layerProxy_;

	public static String serverName_S;
	public static int serverPort_S;
	
	/**
	 * This is where the program starts.  Creates the Networking and Game Threads.  The command line will
	 * take in two arguments.  The Server Address and the port.  If no command line arguments are specified then
	 * the server and port will default to the following:
	 * 
	 * Server = pason1.enel.ucalgary.ca
	 * Port = 6130
	 */
	public static void main(String[] args) throws Exception
	{
		serverName_S = "pason2.enel.ucalgary.ca";
		serverPort_S = 6130;
		
		if(2 == args.length)
		{
			serverName_S = args[0];
			serverPort_S = Integer.parseInt(args[1]);
		}
		
		BattleShipMain main = new BattleShipMain();
		
		main.network_ = new Network(serverName_S, serverPort_S);
		main.ai_ = new BattleShip3DAI();
		
		main.layerProxy_ = new GameLayerAndNetworkingLayerProxy(main.ai_, main.network_);
		
		main.network_.setProxy(main.layerProxy_);
		main.ai_.setProxy(main.layerProxy_);
		main.ai_.start();
	}

}

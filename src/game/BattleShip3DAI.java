/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;

//Java Imports
import java.util.Random;

//BattleShip Imports
import main.GameLayerAndNetworkingLayerProxy;

/*
 * The main game thread, sets up the shot calculator and creates the ship positions.
 */
public class BattleShip3DAI extends Thread
{
	/*
	 * Index 0 is for x dimension
	 * Index 1 is for y dimension
	 * Index 2 is for z dimension
	 */
	private static int[] gameBoardDimensions_S;
	
	private boolean canFire_;
	private boolean lastShotAHit_;
	public static boolean findingShip_;
	private boolean wasShipSunk_;
	private GameLayerAndNetworkingLayerProxy layerProxy_;
	private ShipPositionGenerator model_;
	private ShotCalculator shotCalculator_;
	
	//if a ship was sunk then lastShipSunk_ != ""
	private String lastShipSunk_;
	
	/*
	 * Static function to generate 1 random Point3D.
	 */
	public static Point3D generateRandomPoint3D()
	{
		Random randomGenerator = new Random(System.nanoTime());
		int generatedXCoordinate = randomGenerator.nextInt(gameBoardDimensions_S[0]);
		int generatedYCoordinate = randomGenerator.nextInt(gameBoardDimensions_S[1]);
		int generatedZCoordinate = randomGenerator.nextInt(gameBoardDimensions_S[2]);
		Point3D newPoint = new Point3D(generatedXCoordinate
				, generatedYCoordinate, generatedZCoordinate);
		
		return newPoint;
	}
	
	/*
	 * Default constructor.
	 */
	public BattleShip3DAI()
	{
		gameBoardDimensions_S = null;
		canFire_ = false;
		lastShotAHit_ = false;
		wasShipSunk_ = false;
		findingShip_ = false;
	}
	
	/*
	 * Sets up the gameboardModel which calculates our ship positions.
	 * Ship positions are sent to the networking layer.  Then
	 * we start firing.
	 */
	public void run()
	{
		waitForGameBoardDimensions();
		
		setupGameBoardModel();
		sendShipPositionsToProxyLayer();
		setupShotCalculator();
		
		startFiring();
	}
	
	/*
	 * Method that keeps looping until notified to stop.  Every iteration through the loop
	 * will fire another shot.
	 */
	private void startFiring()
	{
		while(true)
		{
			//wait until we can take a shot.
			waitForFireRequest();
			
			Point3D point;
			if(findingShip_ || wasShipSunk_)
			{
				point = shotCalculator_.sinkShip(lastShotAHit_, lastShipSunk_);
				wasShipSunk_ = false;
			}
			else
			{
				point = shotCalculator_.fire();
			}
		
			sendShotToProxyLayer(point);
			
			//Reset fire flag so only 1 shot gets fired.
			canFire_ = false;
		}
	}
	
	/*
	 * Synchronization function to ensure that the gameboard has dimensions before making
	 * the board.
	 */
	private synchronized void waitForGameBoardDimensions()
	{
		while(gameBoardDimensions_S == null)
		{
			//Make the thread wait until we get ship dimensions.
			try
			{
				this.wait();
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
	
	/*
	 * Synchronization function to ensure we wait between fire requests.
	 */
	private synchronized void waitForFireRequest()
	{
		while(!canFire_)
		{
			//Make the thread wait until we can fire again.
			try
			{
				this.wait();
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
	
	/*
	 * Sets the messaging proxy that will send messages to the networking layer.
	 */
	public void setProxy(GameLayerAndNetworkingLayerProxy proxy)
	{
		layerProxy_ = proxy;
	}
	
	/*
	 * Sets the game board dimensions and notifies waiting threads to wake up.
	 */
	public synchronized void setGameBoardDimensions(int[] dimensions)
	{
		gameBoardDimensions_S = dimensions;
		notifyAll();
	}
	
	/*
	 * Requests a shot and then notifies waiting threads.
	 */
	public synchronized void requestShotAfterMiss()
	{
		canFire_ = true;
		lastShotAHit_ = false;
		lastShipSunk_ = "";
		notifyAll();
	}
	
	/*
	 * Requests a shot and then notifies waiting threads.
	 */
	public synchronized void requestShotAfterHit(boolean wasShipSunk, String shipSunk)
	{
		canFire_ = true;
		
		if(wasShipSunk)
		{
			findingShip_ = false;
			wasShipSunk_ = wasShipSunk;
		}
		else
		{
			findingShip_ = true;
		}
		lastShipSunk_ = shipSunk;
		lastShotAHit_ = true;
		notifyAll();
	}
	
	/*
	 * Sets up the Ship placement model and then creates new ship positions.
	 */
	private void setupGameBoardModel()
	{
		model_ = new ShipPositionGenerator(gameBoardDimensions_S);
		model_.generateNewShipPositions();
	}
	
	/*
	 * Sends the ship positions to the proxy layer.
	 */
	private void sendShipPositionsToProxyLayer()
	{
		String shipPositions = model_.convertShipPositionsToString();
		layerProxy_.sendShipLayoutsToNetworkingLayer(shipPositions);
	}
	
	/*
	 * Sets up the Shot Calculator class.
	 */
	private void setupShotCalculator()
	{
		shotCalculator_ = new ShotCalculator(gameBoardDimensions_S[0], gameBoardDimensions_S[1], gameBoardDimensions_S[2]);
	}
	
	/*
	 * Sends a shot to the proxy.
	 */
	private void sendShotToProxyLayer(Point3D nextShot)
	{
		layerProxy_.sendShotToNetworkingLayer(nextShot.toString());
	}
	

}

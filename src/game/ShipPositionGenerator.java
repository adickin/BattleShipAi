/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;

//Java Imports
import java.util.Random;
import java.util.Vector;

//BattleShipImports
import networking.ServerApi;

/**
 * This class contains all of the ship layout algorithms.
 */
public class ShipPositionGenerator 
{
	private Vector<Point3D> usedShipPoints_;
	private Vector<ShipLayout> ships_;
	private int xDimension_;
	private int yDimension_;
	private int zDimension_;
	
	public ShipPositionGenerator(int[] gameBoardDimensions)
	{
		usedShipPoints_ = new Vector<Point3D>();
		ships_ = new Vector<ShipLayout>();
		
		xDimension_ = gameBoardDimensions[0];
		yDimension_ = gameBoardDimensions[1];
		zDimension_ = gameBoardDimensions[2];
	}
	
	/*
	 * Generates random ship positions.
	 */
	public void generateNewShipPositions()
	{
		int generatedShipDirection = 0;
		Point3D generatedShipReferencePoint = null;
		boolean isShipPositionValid = false;

		//Create the Frigate.
		FrigateLayout frigateShip = null;
		while(!isShipPositionValid)
		{
			generatedShipDirection = generateRandomShipDirection();
			generatedShipReferencePoint = BattleShip3DAI.generateRandomPoint3D();
			frigateShip = new FrigateLayout(generatedShipReferencePoint, generatedShipDirection, xDimension_, yDimension_, zDimension_);
			frigateShip.createFrigateShipLayout(usedShipPoints_);

			isShipPositionValid = frigateShip.isShipPositionValid();
		}
		ships_.add(frigateShip);

		//Create the Submarine
		SubmarineLayout submarineShip = null;
		isShipPositionValid = false;
		while(!isShipPositionValid)
		{
			generatedShipDirection = generateRandomShipDirection();
			generatedShipReferencePoint = BattleShip3DAI.generateRandomPoint3D();
			submarineShip = new SubmarineLayout(generatedShipReferencePoint, generatedShipDirection, xDimension_, yDimension_, zDimension_);
			submarineShip.createSubmarineShipLayout(usedShipPoints_);

			isShipPositionValid = submarineShip.isShipPositionValid();
		}
		ships_.add(submarineShip);
		
		//Create the Destroyer
		DestroyerLayout destroyerShip = null;
		isShipPositionValid = false;
		while(!isShipPositionValid)
		{
			generatedShipDirection = generateRandomShipDirection();
			generatedShipReferencePoint = BattleShip3DAI.generateRandomPoint3D();
			destroyerShip = new DestroyerLayout(generatedShipReferencePoint, generatedShipDirection, xDimension_, yDimension_, zDimension_);
			destroyerShip.createDestroyerShipLayout(usedShipPoints_);

			isShipPositionValid = destroyerShip.isShipPositionValid();
		}
		ships_.add(destroyerShip);
		
		//Create the Battleship
		BattleShipLayout battleShipShip = null;
		isShipPositionValid = false;
		while(!isShipPositionValid)
		{
			generatedShipDirection = generateRandomShipDirection();
			generatedShipReferencePoint = BattleShip3DAI.generateRandomPoint3D();
			battleShipShip = new BattleShipLayout(generatedShipReferencePoint, generatedShipDirection, xDimension_, yDimension_, zDimension_);
			battleShipShip.createBattleShipShipLayout(usedShipPoints_);

			isShipPositionValid = battleShipShip.isShipPositionValid();
		}
		ships_.add(battleShipShip);
		
		//Create the Carrier
		CarrierLayout carrierShip = null;
		isShipPositionValid = false;
		while(!isShipPositionValid)
		{
			generatedShipDirection = generateRandomShipDirection();
			generatedShipReferencePoint = BattleShip3DAI.generateRandomPoint3D();
			carrierShip = new CarrierLayout(generatedShipReferencePoint, generatedShipDirection, xDimension_, yDimension_, zDimension_);
			carrierShip.createCarrierShipLayout(usedShipPoints_);

			isShipPositionValid = carrierShip.isShipPositionValid();
		}
		ships_.add(carrierShip);
	}
	
	/*
	 * Converts all of the created ships into the server formatted string.
	 */
	public String convertShipPositionsToString()
	{
		String shipPositions = "";
		
		//add frigate.
		for(int i = 0; i < ships_.size(); i++)
		{
			if(ServerApi.FRIGATE == ships_.elementAt(i).shipType())
			{
				FrigateLayout frigate = (FrigateLayout) ships_.elementAt(i);
				shipPositions += ServerApi.FRIGATE + ":";
				for(int j = 0; j < FrigateLayout.NUMBER_OF_POINTS_FOR_FRIGATE; j++)
				{
					shipPositions += frigate.shipPoints_[j].toString();
					
					if(FrigateLayout.NUMBER_OF_POINTS_FOR_FRIGATE == (j+1))
					{
						shipPositions += "|";
					}
					else
					{
						shipPositions += ";";
					}	
				}
				break;
			}
		}
		
		//add submarine
		for(int i = 0; i < ships_.size(); i++)
		{
			if(ServerApi.SUBMARINE == ships_.elementAt(i).shipType())
			{
				SubmarineLayout submarine = (SubmarineLayout) ships_.elementAt(i);
				shipPositions += ServerApi.SUBMARINE + ":";
				for(int j = 0; j < SubmarineLayout.NUMBER_OF_POINTS_FOR_SUBMARINE; j++)
				{
					shipPositions += submarine.shipPoints_[j].toString();
					
					if(SubmarineLayout.NUMBER_OF_POINTS_FOR_SUBMARINE == (j+1))
					{
						shipPositions += "|";
					}
					else
					{
						shipPositions += ";";
					}	
				}
				break;
			}
		}
		
		//add Destroyer
		for(int i = 0; i < ships_.size(); i++)
		{
			if(ServerApi.DESTROYER == ships_.elementAt(i).shipType())
			{
				DestroyerLayout destroyer = (DestroyerLayout) ships_.elementAt(i);
				shipPositions += ServerApi.DESTROYER + ":";
				for(int j = 0; j < DestroyerLayout.NUMBER_OF_POINTS_FOR_DESTROYER; j++)
				{
					shipPositions += destroyer.shipPoints_[j].toString();
					
					if(DestroyerLayout.NUMBER_OF_POINTS_FOR_DESTROYER == (j+1))
					{
						shipPositions += "|";
					}
					else
					{
						shipPositions += ";";
					}	
				}
				break;
			}
		}
		
		//add Battleship
		for(int i = 0; i < ships_.size(); i++)
		{
			if(ServerApi.BATTLESHIP == ships_.elementAt(i).shipType())
			{
				BattleShipLayout battleship = (BattleShipLayout) ships_.elementAt(i);
				shipPositions += ServerApi.BATTLESHIP + ":";
				for(int j = 0; j < BattleShipLayout.NUMBER_OF_POINTS_FOR_BATTLESHIP; j++)
				{
					shipPositions += battleship.shipPoints_[j].toString();
					
					if(BattleShipLayout.NUMBER_OF_POINTS_FOR_BATTLESHIP == (j+1))
					{
						shipPositions += "|";
					}
					else
					{
						shipPositions += ";";
					}	
				}
				break;
			}
		}
		
		//add Carrier
		for(int i = 0; i < ships_.size(); i++)
		{
			if(ServerApi.CARRIER == ships_.elementAt(i).shipType())
			{
				CarrierLayout carrier = (CarrierLayout) ships_.elementAt(i);
				shipPositions += ServerApi.CARRIER + ":";
				for(int j = 0; j < CarrierLayout.NUMBER_OF_POINTS_FOR_CARRIER; j++)
				{
					shipPositions += carrier.shipPoints_[j].toString();
					
					if(CarrierLayout.NUMBER_OF_POINTS_FOR_CARRIER == (j+1))
					{
						//do nothing since carrier doesn't need a | at the end of it.
					}
					else
					{
						shipPositions += ";";
					}	
				}
				break;
			}
		}
		
		return shipPositions;
	}
	
	/*
	 * Generates a randomship direction to create the ship direction in.
	 */
	public int generateRandomShipDirection()
	{
		Random randomGenerator = new Random(System.nanoTime());
		int generatedOrientation = randomGenerator.nextInt(2);
		
		return generatedOrientation;
	}
}

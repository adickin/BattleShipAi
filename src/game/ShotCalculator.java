/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;

//Java Imports
import java.util.Random;
import java.util.Vector;

//BattleShip Imports
import networking.ServerApi;

/**
 * The ShotCalculator class is used to determine the next board position to fire upon
 * given the last return fire result.
 */
public class ShotCalculator
{
	private final int INVALID_LOCATION = -1;
	private Vector<Point3D> fiveHundredPointGridOne_;
	private Vector<Point3D> fiveHundredPointGridTwo_;
	private GameboardModel boardModel_;
	private Point3D lastPointFiredOn_;
	private int hitCounter_;
	private int xDimension_;
	private int yDimension_;
	private int zDimension_;
	private boolean keepFiringOnShipVectors_;
	
	private Vector<Vector<Point3D>> shipResolutionVectors_;
	
	/*
	 * Default constructor with board dimensions set to 10x10x10.
	 */
	public ShotCalculator()
	{
		fiveHundredPointGridOne_ = new Vector<Point3D>(0);
		fiveHundredPointGridTwo_ = new Vector<Point3D>(0);
		boardModel_ = new GameboardModel();
		lastPointFiredOn_ = null;
		hitCounter_ = 0;
		keepFiringOnShipVectors_ = false;
		
		shipResolutionVectors_ = new Vector<Vector<Point3D>>(0);
		
		initializeFiringGrids(10,10,10);
	}
	
	/*
	 * Constructor with variable board size.
	 */
	public ShotCalculator(int xDimension, int yDimension, int zDimension)
	{
		fiveHundredPointGridOne_ = new Vector<Point3D>(0);
		fiveHundredPointGridTwo_ = new Vector<Point3D>(0);
		boardModel_ = new GameboardModel();
		lastPointFiredOn_ = null;
		hitCounter_ = 0;
		keepFiringOnShipVectors_ = false;
		
		shipResolutionVectors_ = new Vector<Vector<Point3D>>(0);
		
		xDimension_ = xDimension;
		yDimension_ = yDimension;
		zDimension_ = zDimension;
		
		initializeFiringGrids(xDimension, yDimension, zDimension);
	}
	
	/*
	 * Initializes the two checkerboard 500 point fireing grids to fire upon.
	 * The two grids contain unique points.
	 */
	public void initializeFiringGrids(int xCount, int yCount, int zCount)
	{
		int offset = selectRandomValue(2);
		int gridTwoOffset = switchOffset(offset);
		
		//create grid one
		int startingOffset = offset;
		for(int x = 0;x < xCount;x++)
		{
			if(startingOffset == offset)
			{
				offset = switchOffset(offset);
				startingOffset = offset;
			}
			for(int y = 0;y < yCount;y++)
			{
				for(int z = offset; z < zCount; z += 2)
				{
					fiveHundredPointGridOne_.add(new Point3D(x, y, z));
				}	
				offset = switchOffset(offset);
			}
		}
		
		//create grid two
		startingOffset = gridTwoOffset;
		for(int x = 0;x < xCount;x++)
		{
			if(startingOffset == gridTwoOffset)
			{
				gridTwoOffset = switchOffset(gridTwoOffset);
				startingOffset = gridTwoOffset;
			}
			for(int y = 0;y < yCount;y++)
			{
				for(int z = gridTwoOffset; z < zCount; z += 2)
				{
					fiveHundredPointGridTwo_.add(new Point3D(x, y, z));
				}	
				gridTwoOffset = switchOffset(gridTwoOffset);
			}
		}
	}
	
	/*
	 * Finds a Point3D to fire upon randomly, then sets the board grid point that resembles
	 * that point to fired upon.
	 */
	public Point3D fire()
	{
		Point3D targetPoint = getFirePoint();
		boardModel_.grid_[targetPoint.getX()][targetPoint.getY()][targetPoint.getZ()].setWasFiredUpon();
		lastPointFiredOn_ = targetPoint;
		
		return targetPoint;
	}
	
	/*
	 * Pulls a fire point from one of the two 500 point grids randomly.  If the first
	 * grid is empty then select from the second grid.  As a fail safe this function
	 * will return a random point.
	 */
	private Point3D getFirePoint()
	{
		int randomPoint = 0;
		boolean allowedToRemove = true;
		if(!fiveHundredPointGridOne_.isEmpty())
		{
			Point3D firePoint = null;
			for(int i = 0; i < fiveHundredPointGridOne_.size(); i++)
			{
				randomPoint = selectRandomValue(fiveHundredPointGridOne_.size());
				firePoint =  fiveHundredPointGridOne_.get(randomPoint);
				
				if(boardModel_.grid_[firePoint.getX()][firePoint.getY()][firePoint.getZ()].getWasFiredUpon())
				{
					fiveHundredPointGridOne_.removeElementAt(randomPoint);
					//reset back to start of loop, so we can guarantee all elements get used.
					i = -1;
					if(fiveHundredPointGridOne_.isEmpty())
					{
						allowedToRemove = false;
						break;
					}
				}
			}
			if(allowedToRemove)
			{
				fiveHundredPointGridOne_.removeElementAt(randomPoint);
				return firePoint;
			}
		}
		
		if(!fiveHundredPointGridTwo_.isEmpty())
		{
			Point3D firePoint = null;
			for(int i = 0; i < fiveHundredPointGridTwo_.size(); i ++)
			{
				randomPoint = selectRandomValue(fiveHundredPointGridTwo_.size());
				firePoint =  fiveHundredPointGridTwo_.get(randomPoint);
				
				if(boardModel_.grid_[firePoint.getX()][firePoint.getY()][firePoint.getZ()].getWasFiredUpon())
				{
					fiveHundredPointGridTwo_.removeElementAt(randomPoint);
					//reset back to start of loop, so we can guarantee all elements get used.
					i = -1;
				}
			}
			
			fiveHundredPointGridTwo_.removeElementAt(randomPoint);
			return firePoint;
		}
		
		//If this happens, return random points until loss/win
		return BattleShip3DAI.generateRandomPoint3D();
	}
	
	/*
	 * Algorithm used after a ship has been found using the random fire function.
	 * Fires at points around the lastHit target until a ship is sunk.  Upon sinking
	 * a ship the number of hits is compared with the sunkships length to see if the
	 * ship resolution vectors still contain another ship.
	 */
	public Point3D sinkShip(boolean hit, String shipSunk)
	{
		
		if(!shipSunk.equals(""))
		{
			hitCounter_++;
			boolean wereOtherShipsHit = false;
			//A ship has been sunk.
			if(shipSunk.equals(ServerApi.FRIGATE))
			{
				wereOtherShipsHit = !(hitCounter_ == FrigateLayout.NUMBER_OF_POINTS_FOR_FRIGATE);
				hitCounter_ = hitCounter_ - FrigateLayout.NUMBER_OF_POINTS_FOR_FRIGATE;
			}
			else if(shipSunk.equals(ServerApi.DESTROYER))
			{
				wereOtherShipsHit = !(hitCounter_ == DestroyerLayout.NUMBER_OF_POINTS_FOR_DESTROYER);
				hitCounter_ = hitCounter_ - DestroyerLayout.NUMBER_OF_POINTS_FOR_DESTROYER;
			}
			else if(shipSunk.equals(ServerApi.SUBMARINE))
			{
				wereOtherShipsHit = !(hitCounter_ == SubmarineLayout.NUMBER_OF_POINTS_FOR_SUBMARINE);
				hitCounter_ = hitCounter_ - SubmarineLayout.NUMBER_OF_POINTS_FOR_SUBMARINE;
			}
			else if(shipSunk.equals(ServerApi.BATTLESHIP))
			{
				wereOtherShipsHit = !(hitCounter_ == BattleShipLayout.NUMBER_OF_POINTS_FOR_BATTLESHIP);
				hitCounter_ = hitCounter_ - BattleShipLayout.NUMBER_OF_POINTS_FOR_BATTLESHIP;
			}
			else if(shipSunk.equals(ServerApi.CARRIER))
			{
				wereOtherShipsHit = !(hitCounter_ == CarrierLayout.NUMBER_OF_POINTS_FOR_CARRIER);
				hitCounter_ = hitCounter_ - CarrierLayout.NUMBER_OF_POINTS_FOR_CARRIER;
			}
			
			keepFiringOnShipVectors_ = wereOtherShipsHit;
			
			if(!keepFiringOnShipVectors_)
			{
				//no extra ships were hit while trying to sink the last sunk ship. Clear
				//the ship vectors and recreate empty ones.
				hitCounter_ = 0;
				shipResolutionVectors_ = null;
				shipResolutionVectors_ = new Vector<Vector<Point3D>>(0);
				return fire();
			}
			else
			{
				BattleShip3DAI.findingShip_ = true;
				hitCounter_--;
			}
		}	
		
		if(hit)
		{
			boardModel_.grid_[lastPointFiredOn_.getX()][lastPointFiredOn_.getY()][lastPointFiredOn_.getZ()].setContainsShip();
			
			hitCounter_++;
			Vector<Point3D> pointsAroundLastFiredOnPoint = createShipResolutionVectorAround(lastPointFiredOn_);
			if(pointsAroundLastFiredOnPoint.size() > 0)
			{
				shipResolutionVectors_.addElement(pointsAroundLastFiredOnPoint);
				int randomPoint = selectRandomValue(pointsAroundLastFiredOnPoint.size());
				Point3D firePoint =  pointsAroundLastFiredOnPoint.get(randomPoint);
				pointsAroundLastFiredOnPoint.removeElementAt(randomPoint);
				lastPointFiredOn_ = firePoint;
				if(pointsAroundLastFiredOnPoint.size() == 0)
				{
					shipResolutionVectors_.removeElementAt(shipResolutionVectors_.size()-1);
				}
				boardModel_.grid_[firePoint.getX()][firePoint.getY()][firePoint.getZ()].setWasFiredUpon();
				return firePoint;
			}
			else
			{
				if(shipResolutionVectors_.size() == 0)
				{
					return fire();
				}
				//if no points available around the last fired on point go back to the first step.
				Vector<Point3D> startingFirePoints = shipResolutionVectors_.elementAt(0);
				int randomPoint = selectRandomValue(startingFirePoints.size());
				Point3D firePoint =  startingFirePoints.get(randomPoint);
				startingFirePoints.removeElementAt(randomPoint);
				lastPointFiredOn_ = firePoint;
				if(startingFirePoints.size() == 0)
				{
					shipResolutionVectors_.removeElementAt(0);
				}
				boardModel_.grid_[firePoint.getX()][firePoint.getY()][firePoint.getZ()].setWasFiredUpon();
				return firePoint;
			}	
		}
		else
		{
			if(shipResolutionVectors_.size() == 0)
			{
				return fire();
			}
			Vector<Point3D> startingFirePoints = shipResolutionVectors_.elementAt(shipResolutionVectors_.size()-1);
			int randomPoint = selectRandomValue(startingFirePoints.size());
			Point3D firePoint =  startingFirePoints.get(randomPoint);
			startingFirePoints.removeElementAt(randomPoint);
			lastPointFiredOn_ = firePoint;
			if(startingFirePoints.size() == 0)
			{
				shipResolutionVectors_.removeElementAt(shipResolutionVectors_.size()-1);
			}
			boardModel_.grid_[firePoint.getX()][firePoint.getY()][firePoint.getZ()].setWasFiredUpon();
			return firePoint;
		}
	}
	
	/*
	 * Creates Points around a specified point. Then removes invalid points and points already fired on.
	 */
	private Vector<Point3D> createShipResolutionVectorAround(Point3D point)
	{
		Vector<Point3D> resolutionVector = new Vector<Point3D>();
		
		//create the 6 points around the last hit point.
		Point3D point1 = new Point3D((point.getX()+1), point.getY(), point.getZ());
		Point3D point2 = new Point3D((point.getX()-1), point.getY(), point.getZ());
		Point3D point3 = new Point3D(point.getX(), (point.getY()+1), point.getZ());
		Point3D point4 = new Point3D(point.getX(), (point.getY()-1), point.getZ());
		Point3D point5 = new Point3D(point.getX(), point.getY(), (point.getZ()+1));
		Point3D point6 = new Point3D(point.getX(), point.getY(), (point.getZ()-1));
		
		//checking point 1
		boolean isPointInvalid = ((point1.getX() == INVALID_LOCATION ) || (point1.getX() == xDimension_)) || 
								((point1.getY() == INVALID_LOCATION ) || (point1.getY() == yDimension_)) ||
								((point1.getZ() == INVALID_LOCATION ) || (point1.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point1.getX()][point1.getY()][point1.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point1);
			}
		}
		
		//checking point 2
		isPointInvalid = ((point2.getX() == INVALID_LOCATION ) || (point2.getX() == xDimension_)) || 
						((point2.getY() == INVALID_LOCATION ) || (point2.getY() == yDimension_)) ||
						((point2.getZ() == INVALID_LOCATION ) || (point2.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point2.getX()][point2.getY()][point2.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point2);
			}
		}
		
		//checking point 3
		isPointInvalid = ((point3.getX() == INVALID_LOCATION ) || (point3.getX() == xDimension_)) || 
						((point3.getY() == INVALID_LOCATION ) || (point3.getY() == yDimension_)) ||
						((point3.getZ() == INVALID_LOCATION ) || (point3.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point3.getX()][point3.getY()][point3.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point3);
			}
		}
		
		//checking point 4
		isPointInvalid = ((point4.getX() == INVALID_LOCATION ) || (point4.getX() == xDimension_)) || 
						((point4.getY() == INVALID_LOCATION ) || (point4.getY() == yDimension_)) ||
						((point4.getZ() == INVALID_LOCATION ) || (point4.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point4.getX()][point4.getY()][point4.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point4);
			}
		}
		
		//checking point 5
		isPointInvalid = ((point5.getX() == INVALID_LOCATION ) || (point5.getX() == xDimension_)) || 
						((point5.getY() == INVALID_LOCATION ) || (point5.getY() == yDimension_)) ||
						((point5.getZ() == INVALID_LOCATION ) || (point5.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point5.getX()][point5.getY()][point5.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point5);
			}
		}
		
		//checking point 6
		isPointInvalid = ((point6.getX() == INVALID_LOCATION ) || (point6.getX() == xDimension_)) || 
						((point6.getY() == INVALID_LOCATION ) || (point6.getY() == yDimension_)) ||
						((point6.getZ() == INVALID_LOCATION ) || (point6.getZ() == zDimension_));
		
		if(!isPointInvalid)
		{
			if(!boardModel_.grid_[point6.getX()][point6.getY()][point6.getZ()].getWasFiredUpon())
			{
				resolutionVector.addElement(point6);
			}
		}
		
		return resolutionVector;
	}
	
	/*
	 * Changes the offset between 1 and 0 based on what is passed in.
	 */
	private int switchOffset(int offset)
	{
		if(offset == 1)
		{
			return 0;
		}
		return 1;
	}
	
	/*
	 *  Selects a random value in between -1 and the limit value
	 */
	private int selectRandomValue(int limit)
	{
		Random randomGenerator = new Random(System.currentTimeMillis());
		return randomGenerator.nextInt(limit);			
	}
}

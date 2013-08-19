/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;

//Java Imports
import java.util.Vector;

//BattleShip Imports
import networking.ServerApi;

/**
 * This class creates a Submarine battleship.
 */
public class SubmarineLayout extends ShipLayout
{
	static final int NUMBER_OF_POINTS_FOR_SUBMARINE = 5;
	static final int POINTS_NEEDED_AWAY_FROM_BOUNDARYS = 5;
	
	private int xDimension_;
	private int yDimension_;
	private int zDimension_;
	
	/*
	 * Constructor for the submarine.
	 */
	public SubmarineLayout(Point3D referencePoint, int shipOrientation, int xDimension, int yDimension, int zDimension)
	{
		super(ServerApi.SUBMARINE, referencePoint, NUMBER_OF_POINTS_FOR_SUBMARINE, shipOrientation);
		
		xDimension_ = xDimension;
		yDimension_ = yDimension;
		zDimension_ = zDimension;
	}
	
	/*
	 * Creates the layout for the submarine ship that will fit inside the gameboard. Ensures
	 * that the direction that the points expand from the reference are valid.
	 */
	public void createSubmarineShipLayout(Vector<Point3D> usedPoints)
	{

		if(X_EXPANDING == shipOrientation_)
		{
			verifyXDirectionExpandingReferencePoint();
			shipPoints_[0] = referencePoint_;
			for(int i = 1; i < NUMBER_OF_POINTS_FOR_SUBMARINE; i++)
			{
				int nextX = ( referencePoint_.getX() + i );
				shipPoints_[i] = new Point3D(nextX, referencePoint_.getY(), referencePoint_.getZ());
			}
		}
		else if(Y_EXPANDING == shipOrientation_)
		{
			verifyYDirectionExpandingReferencePoint();
			shipPoints_[0] = referencePoint_;
			for(int i = 1; i < NUMBER_OF_POINTS_FOR_SUBMARINE; i++)
			{
				int nextY = ( referencePoint_.getY() + i );
				shipPoints_[i] = new Point3D(referencePoint_.getX(), nextY, referencePoint_.getZ());
			}
		}
		else if(Z_EXPANDING == shipOrientation_)
		{
			verifyZDirectionExpandingReferencePoint();
			shipPoints_[0] = referencePoint_;
			for(int i = 1; i < NUMBER_OF_POINTS_FOR_SUBMARINE; i++)
			{
				int nextZ = ( referencePoint_.getZ() + i );
				shipPoints_[i] = new Point3D(referencePoint_.getX(), referencePoint_.getY(), nextZ);
			}
		}
		
		validateShip(usedPoints);
		
		//If ship position is valid then add the points to the used points vector.
		if(shipPositionIsValid_)
		{
			for(int i = 0; i < NUMBER_OF_POINTS_FOR_SUBMARINE; i++)
			{
				usedPoints.add(shipPoints_[i]);
			}
		}
	}
	
	/*
	 * Ensures that while expanding the coordinate in the x direction, that the last
	 * coordinate is 0 <= x <= xDimension-1.  
	 */
	private void verifyXDirectionExpandingReferencePoint()
	{
		int distanceNeededAwayFromBoundaryInXDirection = (xDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		if(referencePoint_.getX() > distanceNeededAwayFromBoundaryInXDirection)
		{
			referencePoint_.setX(distanceNeededAwayFromBoundaryInXDirection);
		}
	}
	
	/*
	 * Ensures that while expanding the coordinate in the y direction, that the last
	 * coordinate is 0 <= y <= yDimension-1.  
	 */
	private void verifyYDirectionExpandingReferencePoint()
	{
		int distanceNeededAwayFromBoundaryInYDirection = (yDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		if(referencePoint_.getY() > distanceNeededAwayFromBoundaryInYDirection)
		{
			referencePoint_.setY(distanceNeededAwayFromBoundaryInYDirection);
		}
	}
	
	/*
	 * Ensures that while expanding the coordinate in the z direction, that the last
	 * coordinate is 0 <= z <= zDimension-1.  
	 */
	private void verifyZDirectionExpandingReferencePoint()
	{
		int distanceNeededAwayFromBoundaryInZDirection = (zDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		if(referencePoint_.getZ() > distanceNeededAwayFromBoundaryInZDirection)
		{
			referencePoint_.setZ(distanceNeededAwayFromBoundaryInZDirection);
		}
	}
	
	/*
	 * Iterate through the usedPoints and ensures that this ship does not overlap any
	 * of the other ships.
	 */
	private void validateShip(Vector<Point3D> usedPoints)
	{
		for(int i = 0; i < usedPoints.size(); i++)
		{
			for(int j = 0; j < NUMBER_OF_POINTS_FOR_SUBMARINE; j++)
			{
				if(usedPoints.elementAt(i).isEqualTo(shipPoints_[j]))
				{
					shipPositionIsValid_ = false;
					return;
				}
			}
		}
		shipPositionIsValid_ = true;
	}
}
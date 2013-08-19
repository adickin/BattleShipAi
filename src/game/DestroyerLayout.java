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
 * This class creates a Destroyer battleship.
 */
public class DestroyerLayout extends ShipLayout
{
	static final int NUMBER_OF_POINTS_FOR_DESTROYER = 5;
	static final int POINTS_NEEDED_AWAY_FROM_BOUNDARYS = 2;
	
	private int xDimension_;
	private int yDimension_;
	private int zDimension_;
	
	/*
	 * Constructor for the Destroyer.  The referencePoint refers to the middle point in the
	 * destroyer.
	 */
	public DestroyerLayout(Point3D referencePoint, int shipOrientation, int xDimension, int yDimension, int zDimension)
	{
		super(ServerApi.DESTROYER, referencePoint, NUMBER_OF_POINTS_FOR_DESTROYER, shipOrientation);
		xDimension_ = xDimension;
		yDimension_ = yDimension;
		zDimension_ = zDimension;
	}
	
	/*
	 * Creates the layout for the destroyer ship that will fit inside the gameboard. Ensures
	 * that the direction that the points expand from the reference are valid.
	 */
	public void createDestroyerShipLayout(Vector<Point3D> usedPoints)
	{

		if(X_EXPANDING == shipOrientation_)
		{
			verifyXDirectionExpandingReferencePoint();
			verifyYDirectionExpandingReferencePoint();
			verifyZDirectionExpandingReferencePoint();
			
			shipPoints_[0] = referencePoint_;
			shipPoints_[1] = new Point3D((referencePoint_.getX()+1), referencePoint_.getY()
					, referencePoint_.getZ());
			shipPoints_[2] = new Point3D((referencePoint_.getX()-1), referencePoint_.getY()
					, referencePoint_.getZ());
			shipPoints_[3] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()+1)
					, referencePoint_.getZ());
			shipPoints_[4] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()-1)
					, referencePoint_.getZ());
			
		}
		else if(Y_EXPANDING == shipOrientation_)
		{
			verifyXDirectionExpandingReferencePoint();
			verifyYDirectionExpandingReferencePoint();
			verifyZDirectionExpandingReferencePoint();
			
			shipPoints_[0] = referencePoint_;
			shipPoints_[1] = new Point3D((referencePoint_.getX()+1), referencePoint_.getY()
					, referencePoint_.getZ());
			shipPoints_[2] = new Point3D((referencePoint_.getX()-1), referencePoint_.getY()
					, referencePoint_.getZ());
			shipPoints_[3] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()+1)
					, referencePoint_.getZ());
			shipPoints_[4] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()-1)
					, referencePoint_.getZ());

		}
		else if(Z_EXPANDING == shipOrientation_)
		{
			verifyXDirectionExpandingReferencePoint();
			verifyYDirectionExpandingReferencePoint();
			verifyZDirectionExpandingReferencePoint();
			
			shipPoints_[0] = referencePoint_;
			shipPoints_[1] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
					, (referencePoint_.getZ()+1));
			shipPoints_[2] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
					, (referencePoint_.getZ()-1));
			shipPoints_[3] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()+1)
					, referencePoint_.getZ());
			shipPoints_[4] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()-1)
					, referencePoint_.getZ());

		}
		
		validateShip(usedPoints);
		
		//If ship position is valid then add the points to the used points vector.
		if(shipPositionIsValid_)
		{
			for(int i = 0; i < NUMBER_OF_POINTS_FOR_DESTROYER; i++)
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
		int distanceNeededAwayFromBoundaryInXTopDirection = (xDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		int distanceNeededAwayFromLowerBoundaryInXDirection = 1;
		if(referencePoint_.getX() > distanceNeededAwayFromBoundaryInXTopDirection)
		{
			referencePoint_.setX(distanceNeededAwayFromBoundaryInXTopDirection);
		}
		else if(referencePoint_.getX() < distanceNeededAwayFromLowerBoundaryInXDirection)
		{
			referencePoint_.setX(distanceNeededAwayFromLowerBoundaryInXDirection);
		}
	}
	
	/*
	 * Ensures that while expanding the coordinate in the y direction, that the last
	 * coordinate is 0 <= y <= yDimension-1.  
	 */
	private void verifyYDirectionExpandingReferencePoint()
	{
		int distanceNeededAwayFromBoundaryInYTopDirection = (yDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		int distanceNeededAwayFromLowerBoundaryInYDirection = 1;
		if(referencePoint_.getY() > distanceNeededAwayFromBoundaryInYTopDirection)
		{
			referencePoint_.setY(distanceNeededAwayFromBoundaryInYTopDirection);
		}
		else if(referencePoint_.getY() < distanceNeededAwayFromLowerBoundaryInYDirection)
		{
			referencePoint_.setY(distanceNeededAwayFromLowerBoundaryInYDirection);
		}
	}
	
	/*
	 * Ensures that while expanding the coordinate in the z direction, that the last
	 * coordinate is 0 <= z <= zDimension-1.  
	 */
	private void verifyZDirectionExpandingReferencePoint()
	{
		int distanceNeededAwayFromBoundaryInZTopDirection = (zDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		int distanceNeededAwayFromLowerBoundaryInZDirection = 1;
		if(referencePoint_.getZ() > distanceNeededAwayFromBoundaryInZTopDirection)
		{
			referencePoint_.setZ(distanceNeededAwayFromBoundaryInZTopDirection);
		}
		else if(referencePoint_.getZ() < distanceNeededAwayFromLowerBoundaryInZDirection)
		{
			referencePoint_.setZ(distanceNeededAwayFromLowerBoundaryInZDirection);
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
			for(int j = 0; j < NUMBER_OF_POINTS_FOR_DESTROYER; j++)
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
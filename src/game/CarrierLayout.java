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
 * This class creates a carrier layout.
 */
public class CarrierLayout extends ShipLayout
{
	static final int NUMBER_OF_POINTS_FOR_CARRIER = 13;
	static final int POINTS_NEEDED_AWAY_FROM_BOUNDARYS = 3;
	
	private int xDimension_;
	private int yDimension_;
	private int zDimension_;
	
	/*
	 * Default constructor for the carrier. The reference point is center of the carrier.
	 */
	public CarrierLayout(Point3D referencePoint, int shipOrientation, int xDimension, int yDimension, int zDimension)
	{
		super(ServerApi.CARRIER, referencePoint, NUMBER_OF_POINTS_FOR_CARRIER, shipOrientation);
		
		xDimension_ = xDimension;
		yDimension_ = yDimension;
		zDimension_ = zDimension;
	}
	
	/*
	 * Creates the layout for the Carrier ship that will fit inside the gameboard. Ensures
	 * that the direction that the points expand from the reference are valid.  Ship
	 * orientation means nothing to carriers since they are equal in all directions.
	 */
	public void createCarrierShipLayout(Vector<Point3D> usedPoints)
	{
		
		verifyXDirectionExpandingReferencePoint();
		verifyYDirectionExpandingReferencePoint();
		verifyZDirectionExpandingReferencePoint();
		
		shipPoints_[0] = referencePoint_;
		shipPoints_[1] = new Point3D((referencePoint_.getX()+1), referencePoint_.getY()
				, referencePoint_.getZ());
		shipPoints_[2] = new Point3D((referencePoint_.getX()+2), referencePoint_.getY()
				, referencePoint_.getZ());
		shipPoints_[3] = new Point3D((referencePoint_.getX()-1), referencePoint_.getY()
				, referencePoint_.getZ());
		shipPoints_[4] = new Point3D((referencePoint_.getX()-2), referencePoint_.getY()
				, referencePoint_.getZ());
		shipPoints_[5] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()+1)
				, referencePoint_.getZ());
		shipPoints_[6] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()+2)
				, referencePoint_.getZ());
		shipPoints_[7] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()-1)
				, referencePoint_.getZ());
		shipPoints_[8] = new Point3D(referencePoint_.getX(), (referencePoint_.getY()-2)
				, referencePoint_.getZ());
		shipPoints_[9] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
				, (referencePoint_.getZ()+1));
		shipPoints_[10] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
				, (referencePoint_.getZ()+2));
		shipPoints_[11] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
				, (referencePoint_.getZ()-1));
		shipPoints_[12] = new Point3D(referencePoint_.getX(), referencePoint_.getY()
				, (referencePoint_.getZ()-2));

		validateShip(usedPoints);
		
		//If ship position is valid then add the points to the used points vector.
		if(shipPositionIsValid_)
		{
			for(int i = 0; i < NUMBER_OF_POINTS_FOR_CARRIER; i++)
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
		int distanceNeededAwayFromLowerBoundaryInXDirection = 2;
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
		int distanceNeededAwayFromTopBoundaryInYDirection = (yDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		int distanceNeededAwayFromLowerBoundaryInYDirection = 2;
		if(referencePoint_.getY() > distanceNeededAwayFromTopBoundaryInYDirection)
		{
			referencePoint_.setY(distanceNeededAwayFromTopBoundaryInYDirection);
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
		int distanceNeededAwayFromTopBoundaryInZDirection = (zDimension_ - POINTS_NEEDED_AWAY_FROM_BOUNDARYS);
		int distanceNeededAwayFromLowerBoundaryInZDirection = 2;
		if(referencePoint_.getZ() > distanceNeededAwayFromTopBoundaryInZDirection)
		{
			referencePoint_.setZ(distanceNeededAwayFromTopBoundaryInZDirection);
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
			for(int j = 0; j < NUMBER_OF_POINTS_FOR_CARRIER; j++)
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
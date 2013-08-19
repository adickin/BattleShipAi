/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;

/**
 * Parent class for all ships.  Contains attributes that apply to all ships. 
 */
public class ShipLayout 
{

	public static final int X_EXPANDING = 0;
	public static final int Y_EXPANDING = 1;
	public static final int Z_EXPANDING = 2;
	
	protected String battleShipType_;
	protected Point3D referencePoint_;
	protected Point3D[] shipPoints_;
	protected int shipOrientation_;
	protected boolean shipPositionIsValid_;
	
	protected ShipLayout(String type, Point3D referencePoint, int numberOfPoints, int orientation)
	{
		battleShipType_ = type;
		referencePoint_ = referencePoint;
		shipPoints_ = new Point3D[numberOfPoints];
		shipOrientation_ = orientation;
		shipPositionIsValid_ = false;
	}
	
	public boolean isShipPositionValid()
	{
		return shipPositionIsValid_;
	}
	
	public String shipType()
	{
		return battleShipType_;
	}
}

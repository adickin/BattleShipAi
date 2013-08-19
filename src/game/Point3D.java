/**
 * Author: Francis Levesque
 * Team: Ballmer's Peakin
 */
package game;

/*
 * This is a helper class that will be used to contain 
 * information for every point on the battle grid. 
 */
public class Point3D 
{
	private int xPoint_;
	private int yPoint_;
	private int zPoint_;
	private boolean wasFiredUpon_;
	private boolean containsShip_;
	
	// Constructor: initializes a point with the passes in location values
	public Point3D(int x, int y, int z)
	{
		xPoint_ = x;
		yPoint_ = y;
		zPoint_ = z;	
		wasFiredUpon_ = false;
		containsShip_ = false;
	}
	
	// Sets a flag to indicate that the point has been fired upon
	public void setWasFiredUpon()
	{
		wasFiredUpon_ = true;
		return ;
	}
	
	// Returns if the point has been fired upon or not
	public boolean getWasFiredUpon()
	{
		return wasFiredUpon_;
	}
	
	// Sets a flag to indicate that the Point has been fired upon
	public void setContainsShip()
	{
		containsShip_ = true;
		return ;
	}
	
	// Returns if the point contains a ship or not
	public boolean getContainsShip()
	{
		return containsShip_;
	}
	
	/*
	 * Returns the current xPoint of the Point3D
	 */
	public int getX()
	{
		return xPoint_;
	}
	
	/*
	 * Returns the current yPoint of the Point3D
	 */
	public int getY()
	{
		return yPoint_;
	}
	
	/*
	 * Returns the current zPoint of the Point3D
	 */
	public int getZ()
	{
		return zPoint_;
	}
	
	/*
	 * Sets the xPoint to a new value
	 */
	public void setX(int newX)
	{
		xPoint_ = newX;
	}
	
	/*
	 * Sets the yPoint to a new value.
	 */
	public void setY(int newY)
	{
		yPoint_ = newY;
	}
	
	/*
	 * Sets the zPoint to a new value
	 */
	public void setZ(int newZ)
	{
		zPoint_ = newZ;
	}
	
	/*
	 * converts the Point3D into string formatted for the battleship server.
	 */
	public String toString()
	{
		String pointString = "";
		pointString += xPoint_ + ",";
		pointString += yPoint_ + ",";
		pointString += zPoint_;
		
		return pointString;
	}
	
	/*
	 * Compares to Point3D's.
	 */
	public boolean isEqualTo(Point3D pointTwo)
	{
		boolean equal = true;
		
		equal &= (this.xPoint_ == pointTwo.xPoint_);
		equal &= (this.yPoint_ == pointTwo.yPoint_);
		equal &= (this.zPoint_ == pointTwo.zPoint_);
		
		return equal;
	}
}

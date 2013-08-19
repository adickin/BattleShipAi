/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package game;


/**
 * Creates the gameboard model of XxYxZ of the battleship playing field.
 */
public class GameboardModel 
{
	public Point3D[][][] grid_;
	
	// Default Constructor
	public GameboardModel()
	{
		grid_ = new Point3D[10][10][10];
		initializeGrid(10, 10, 10);
	}
	
	/*
	 * Constructor of the gameboard model.
	 */
	public GameboardModel(int gridX, int gridY, int gridZ)
	{
		grid_ = new Point3D[gridX][gridY][gridZ];
		initializeGrid(gridX, gridY, gridZ);
	}
	
	/*
	 * initializes the battlegrid.
	 */
	private void initializeGrid(int gridX, int gridY, int gridZ)
	{
		for(int countX = 0;countX < gridX;countX++)
		{
			for(int countY = 0;countY < gridY;countY++)
			{
				for(int countZ = 0;countZ < gridZ;countZ++)
				{
					grid_[countX][countY][countZ] = new Point3D(countX, countY, countZ);					
				}
			}
		}
	}
	

}

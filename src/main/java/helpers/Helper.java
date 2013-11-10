package helpers;

import java.util.LinkedList;
import java.util.List;

import model.Direction;
import model.Trooper;
import model.World;

public class Helper
{
	
	public static Helper INSTANCE = new Helper();
	
	// with borders
	private final int[][][][] distances = new int[Constants.WORLD_WIDTH + 2][Constants.WORLD_HEIGHT + 2][Constants.WORLD_WIDTH + 2][Constants.WORLD_HEIGHT + 2];
	
	public List<Trooper> findWoundedTeammates(World world)
	{
		List<Trooper> result = new LinkedList<Trooper>();
		for (Trooper trooper : world.getTroopers())
		{
			if (trooper.isTeammate()
			        && trooper.getHitpoints() <= Constants.WOUNDED_HP)
			{
				result.add(trooper);
			}
		}
		return result;
	}
	
	public Direction getDirectionForPath(int xfrom, int yfrom, int xto, int yto)
	{
		int distanceFromHere = getDistance(xfrom, yfrom, xto, yto);
		if (distanceFromHere == getDistance(xfrom + 1, yfrom, xto, yto) + 1)
		{
			return Direction.EAST;
		}
		if (distanceFromHere == getDistance(xfrom - 1, yfrom, xto, yto) + 1)
		{
			return Direction.WEST;
		}
		if (distanceFromHere == getDistance(xfrom, yfrom - 1, xto, yto) + 1)
		{
			return Direction.NORTH;
		}
		return Direction.SOUTH;
	}
	
	public int getDistance(int xfrom, int yfrom, int xto, int yto)
	{
		return distances[xfrom - 1][yfrom - 1][xto - 1][yto - 1];
	}
	
	public void init(World world)
	{
		// TODO: write a method for pre-calculating distances between cells
		// considering impassable cells
		for (int x1 = 0; x1 < Constants.WORLD_WIDTH; x1++)
		{
			for (int y1 = 0; y1 < Constants.WORLD_HEIGHT; y1++)
			{
				calcDistanceRecurs(x1, y1, x1, y1, world);
				
			}
		}
		
	}
	
	private void calcDistanceRecurs(int xfrom, int yfrom, int xto, int yto, World world)
	{
	}
	
}

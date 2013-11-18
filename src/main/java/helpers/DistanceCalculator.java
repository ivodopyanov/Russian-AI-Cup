/**
 * 
 */
package helpers;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.CellType;
import model.Trooper;
import model.World;

import com.google.common.collect.Lists;

/**
 * @author ivodopyanov
 * @since 11 ﾐｽﾐｾﾑ紹ｱ. 2013 ﾐｳ.
 * 
 */
public class DistanceCalculator
{
	
	public static class DistanceCellComparator implements Comparator<Cell>
	{
		
		private final Cell pivot;
		private final World world;
		
		public DistanceCellComparator(Cell pivot, World world)
		{
			this.pivot = pivot;
			this.world = world;
		}
		
		@Override
		public int compare(Cell o1, Cell o2)
		{
			return INSTANCE.getDistance(pivot, o1, world)
			        - INSTANCE.getDistance(pivot, o2, world);
		}
	}
	
	public static final DistanceCalculator INSTANCE = new DistanceCalculator();
	
	// ﾑ�ﾐｳﾑ�ｰﾐｽﾐｸﾑ�ｰﾐｼﾐｸ. ﾐ酉�ｰﾐｽﾐｸﾑ��ﾐｸﾐｼﾐｵﾑ紗�distance=999
	private final boolean initialized = false;
	private static int NOT_PASSABLE = 999;
	private static int NOT_VISITED = 9999;
	
	public int getDistance(Cell start, Cell end, World world)
	{
		int[][] distanceToCells = new int[world.getWidth()][world.getHeight()];
		for (int x = 0; x < world.getWidth(); x++)
		{
			for (int y = 0; y < world.getHeight(); y++)
			{
				distanceToCells[x][y] = NOT_VISITED;
			}
		}
		LinkedList<Cell> nextCells = new LinkedList<Cell>();
		nextCells.add(start);
		calcDistanceRecurs(start, end, nextCells, distanceToCells, world, 0);
		return distanceToCells[end.getX()][end.getY()];
	}
	
	public int getDistance(int x1, int y1, int x2, int y2, World world)
	{
		return getDistance(new Cell(x1, y2), new Cell(x2, y2), world);
	}
	
	public List<Cell> getPath(Cell start, Cell end, World world)
	{
		int[][] distanceToCells = new int[world.getWidth()][world.getHeight()];
		for (int x = 0; x < world.getWidth(); x++)
		{
			for (int y = 0; y < world.getHeight(); y++)
			{
				distanceToCells[x][y] = NOT_VISITED;
			}
		}
		LinkedList<Cell> nextCells = new LinkedList<Cell>();
		nextCells.add(start);
		calcDistanceRecurs(start, end, nextCells, distanceToCells, world, 0);
		return findPathsFromDistanceTable(start, end, distanceToCells, world);
	}
	
	private void calcDistanceRecurs(Cell start, Cell end, LinkedList<Cell> cellsToVisit, int[][] distanceToCells, World world, int distance)
	{
		LinkedList<Cell> nextCells = new LinkedList<Cell>();
		for (Cell cell : cellsToVisit)
		{
			if (!isPassable(cell, world) && !cell.equals(start)
			        && !cell.equals(end))
			{
				distanceToCells[cell.getX()][cell.getY()] = NOT_PASSABLE;
				continue;
			}
			if (distanceToCells[cell.getX()][cell.getY()] != NOT_VISITED)
			{
				continue;
			}
			distanceToCells[cell.getX()][cell.getY()] = distance;
			if (end.equals(cell))
			{
				return;
			}
			if (cell.getX() != world.getWidth() - 1)
			{
				nextCells.add(new Cell(cell.getX() + 1, cell.getY()));
			}
			if (cell.getX() != 0)
			{
				nextCells.add(new Cell(cell.getX() - 1, cell.getY()));
			}
			if (cell.getY() != world.getHeight() - 1)
			{
				nextCells.add(new Cell(cell.getX(), cell.getY() + 1));
			}
			if (cell.getY() != 0)
			{
				nextCells.add(new Cell(cell.getX(), cell.getY() - 1));
			}
		}
		if (!nextCells.isEmpty())
		{
			calcDistanceRecurs(start, end, nextCells, distanceToCells, world,
			        distance + 1);
		}
	}
	
	private Cell findNextCellForPath(Cell current, int[][] distanceToCells, World world)
	{
		Integer distanceToCurrent = distanceToCells[current.getX()][current
		        .getY()];
		if (current.getX() != 0
		        && distanceToCells[current.getX() - 1][current.getY()] == distanceToCurrent - 1)
		{
			return new Cell(current.getX() - 1, current.getY());
		}
		if (current.getX() != world.getWidth() - 1
		        && distanceToCells[current.getX() + 1][current.getY()] == distanceToCurrent - 1)
		{
			return new Cell(current.getX() + 1, current.getY());
		}
		if (current.getY() != 0
		        && distanceToCells[current.getX()][current.getY() - 1] == distanceToCurrent - 1)
		{
			return new Cell(current.getX(), current.getY() - 1);
		}
		if (current.getY() != world.getHeight() - 1
		        && distanceToCells[current.getX()][current.getY() + 1] == distanceToCurrent - 1)
		{
			return new Cell(current.getX(), current.getY() + 1);
		}
		return null;
	}
	
	private List<Cell> findPathsFromDistanceTable(Cell start, Cell end, int[][] distanceToCells, World world)
	{
		List<Cell> path = Lists.newArrayList();
		for (Cell nextCell = end; nextCell != null; nextCell = findNextCellForPath(
		        nextCell, distanceToCells, world))
		{
			path.add(nextCell);
		}
		Collections.reverse(path);
		return path;
		
	}
	
	private boolean isPassable(Cell cell, World world)
	{
		for (Trooper trooper : world.getTroopers())
		{
			if (trooper.getX() == cell.getX() && trooper.getY() == cell.getY())
			{
				return false;
			}
		}
		return CellType.FREE.equals(world.getCells()[cell.getX()][cell.getY()]);
	}
}

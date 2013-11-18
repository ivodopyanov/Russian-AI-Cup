package helpers;

import model.Bonus;
import model.CellType;
import model.Player;
import model.Trooper;
import model.World;

import org.junit.Before;
import org.junit.Test;

public class DistanceCalculatorTest
{
	
	private World world;
	
	@Before
	public void initWorld()
	{
		CellType[][] cells = new CellType[30][20];
		for (int x = 0; x < 30; x++)
		{
			for (int y = 0; y < 20; y++)
			{
				cells[x][y] = CellType.FREE;
			}
		}
		world = new World(0, 30, 20, new Player[0], new Trooper[0],
		        new Bonus[0], cells, new boolean[0]);
	}
	
	@Test
	public void testGetPath()
	{
		DistanceCalculator.INSTANCE.getPath(new Cell(29, 18), new Cell(28, 17),
		        world);
	}
}

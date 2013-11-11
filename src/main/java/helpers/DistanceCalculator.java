/**
 * 
 */
package helpers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.CellType;
import model.World;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class DistanceCalculator
{
    public static class DistanceCellComparator implements Comparator<Cell>
    {
        private Cell pivot;

        @Override
        public int compare(Cell o1, Cell o2)
        {
            return INSTANCE.getDistance(pivot, o1) - INSTANCE.getDistance(pivot, o2);
        }

        public void reset(Cell pivot)
        {
            this.pivot = pivot;
        }

    }

    public static final DistanceCalculator INSTANCE = new DistanceCalculator();

    public static final DistanceCellComparator COMPARATOR = new DistanceCellComparator();

    // с границами. Границы имеют distance=999
    private final int[][][][] distances = new int[Constants.WORLD_WIDTH + 2][Constants.WORLD_HEIGHT + 2][Constants.WORLD_WIDTH + 2][Constants.WORLD_HEIGHT + 2];
    private boolean initialized = false;

    private DistanceCalculator()
    {
        for (int x1 = 0; x1 < Constants.WORLD_WIDTH + 2; x1++)
        {
            for (int y1 = 0; y1 < Constants.WORLD_HEIGHT + 2; y1++)
            {
                for (int x2 = 0; x2 < Constants.WORLD_WIDTH + 2; x2++)
                {
                    for (int y2 = 0; y2 < Constants.WORLD_HEIGHT + 2; y2++)
                    {
                        distances[x1][y1][x2][y2] = -1;
                    }
                }
            }
        }
    }

    public int getDistance(Cell cell1, Cell cell2)
    {
        return distances[cell1.getX() - 1][cell1.getY() - 1][cell2.getX() - 1][cell2.getY() - 1];
    }

    public int getDistance(int x1, int y1, int x2, int y2)
    {
        return distances[x1 - 1][y1 - 1][x2 - 1][y2 - 1];
    }

    public void init(World world)
    {
        for (int x = 0; x < Constants.WORLD_WIDTH; x++)
        {
            for (int y = 0; y < Constants.WORLD_HEIGHT; y++)
            {
                Cell pivot = new Cell(x, y);
                calcDistanceRecurs(pivot, Arrays.asList(pivot), world, 0);
            }
        }
        initialized = true;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    private void calcDistanceRecurs(Cell pivot, List<Cell> cells, World world, int distance)
    {
        List<Cell> nextCells = new LinkedList<Cell>();
        for (Cell cell : cells)
        {
            boolean impassable = !CellType.FREE.equals(world.getCells()[cell.getX()][cell.getY()]) || cell.getX() == -1
                    || cell.getX() == Constants.WORLD_WIDTH || cell.getY() == -1
                    || cell.getY() == Constants.WORLD_HEIGHT;
            if (distance > 0)
            {
                if (impassable)
                {
                    distances[pivot.getX() + 1][pivot.getY() + 1][cell.getX() + 1][cell.getY() + 1] = 999;
                }
                else
                {
                    distances[pivot.getX() + 1][pivot.getY() + 1][cell.getX() + 1][cell.getY() + 1] = distance;
                }
            }

            else

            if (!impassable)
            {
                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        if (distances[pivot.getX() + 1][pivot.getY() + 1][cell.getX() + 1 + i][cell.getY() + 1 + j] == -1)
                        {
                            nextCells.add(new Cell(cell.getX() + i, cell.getY() + j));
                        }
                    }
                }
            }
            calcDistanceRecurs(pivot, cells, world, distance + 1);
        }
    }
}
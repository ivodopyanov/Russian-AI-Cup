/**
 * 
 */
package helpers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;

import model.CellType;
import model.Trooper;
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
    private boolean initialized = false;

    public int getDistance(Cell start, Cell end)
    {
        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        nextCells.add(start);
        while (!nextCells.isEmpty())
        {

        }
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

    private void calcDistanceRecurs(Cell start, Cell end, LinkedList<Cell> cellsToVisit, Set<Cell> visitedCells,
            World world, int distance)
    {
        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        for (Cell cell : cellsToVisit)
        {
            visitedCells.add(cell);
            if (!isPassable(cell, world))
            {
                continue;
            }

            for (int x = cell.getX() - 1; x <= cell.getX() + 1; x++)
            {
                if (x == -1 || x == world.getWidth())
                {
                    continue;
                }
                for (int y = cell.getY() - 1; y <= cell.getY() + 1; y++)
                {
                    if (y == -1 || y == world.getHeight())
                    {
                        continue;
                    }

                    Cell nextCell = new Cell(x, y);
                    if (visitedCells.contains(nextCell))
                    {
                        continue;
                    }
                    nextCells.add(nextCell);
                }
            }
        }
        calcDistanceRecurs(start, end, nextCells, visitedCells, world, distance + 1);
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
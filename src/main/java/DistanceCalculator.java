/**
 * 
 */

import java.util.*;

import model.CellType;
import model.Trooper;
import model.World;

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
        private final boolean withTroopers;

        public DistanceCellComparator(Cell pivot, World world, boolean withTroopers)
        {
            this.pivot = pivot;
            this.world = world;
            this.withTroopers = withTroopers;
        }

        @Override
        public int compare(Cell o1, Cell o2)
        {
            return INSTANCE.getDistance(pivot, o1, world, withTroopers)
                    - INSTANCE.getDistance(pivot, o2, world, withTroopers);
        }
    }

    public static final DistanceCalculator INSTANCE = new DistanceCalculator();

    // ﾑ�ﾐｳﾑ�ｰﾐｽﾐｸﾑ�ｰﾐｼﾐｸ. ﾐ酉�ｰﾐｽﾐｸﾑ��ﾐｸﾐｼﾐｵﾑ紗�distance=999
    private static int NOT_PASSABLE = 999;
    private static int NOT_VISITED = 9999;

    public int getDistance(Cell start, Cell end, World world, boolean withTroopers)
    {
        int[][] distanceToCells = new int[world.getWidth() + 2][world.getHeight() + 2];
        for (int x = 0; x < world.getWidth(); x++)
        {
            for (int y = 0; y < world.getHeight(); y++)
            {
                distanceToCells[x][y] = NOT_VISITED;
            }
        }
        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        nextCells.add(start);
        calcDistanceRecurs(start, end, nextCells, distanceToCells, world, 0, withTroopers);
        return distanceToCells[end.getX()][end.getY()];
    }

    public int getDistance(int x1, int y1, int x2, int y2, World world, boolean withTroopers)
    {
        return getDistance(Cell.create(x1, y2), Cell.create(x2, y2), world, withTroopers);
    }

    public List<Cell> getPath(Cell start, Cell end, World world, boolean withTroopers)
    {
        int[][] distanceToCells = new int[world.getWidth() + 2][world.getHeight() + 2];
        for (int x = 0; x < world.getWidth(); x++)
        {
            for (int y = 0; y < world.getHeight(); y++)
            {
                distanceToCells[x][y] = NOT_VISITED;
            }
        }
        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        nextCells.add(start);
        calcDistanceRecurs(start, end, nextCells, distanceToCells, world, 0, withTroopers);
        return findPathsFromDistanceTable(start, end, distanceToCells, world);
    }

    public boolean isNeighbourCell(int x1, int y1, int x2, int y2)
    {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    private void calcDistanceRecurs(Cell start, Cell end, LinkedList<Cell> cellsToVisit, int[][] distanceToCells,
            World world, int distance, boolean withTroopers)
    {
        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        for (Cell cell : cellsToVisit)
        {
            if (!isPassable(cell, world, withTroopers) && !cell.equals(start) && !cell.equals(end))
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
                nextCells.add(Cell.create(cell.getX() + 1, cell.getY()));
            }
            if (cell.getX() != 0)
            {
                nextCells.add(Cell.create(cell.getX() - 1, cell.getY()));
            }
            if (cell.getY() != world.getHeight() - 1)
            {
                nextCells.add(Cell.create(cell.getX(), cell.getY() + 1));
            }
            if (cell.getY() != 0)
            {
                nextCells.add(Cell.create(cell.getX(), cell.getY() - 1));
            }
        }
        if (!nextCells.isEmpty())
        {
            calcDistanceRecurs(start, end, nextCells, distanceToCells, world, distance + 1, withTroopers);
        }
    }

    private Cell findNextCellForPath(Cell current, int[][] distanceToCells, World world)
    {
        Integer distanceToCurrent = distanceToCells[current.getX()][current.getY()];
        if (current.getX() != 0 && distanceToCells[current.getX() - 1][current.getY()] == distanceToCurrent - 1)
        {
            return Cell.create(current.getX() - 1, current.getY());
        }
        if (current.getX() != world.getWidth() - 1
                && distanceToCells[current.getX() + 1][current.getY()] == distanceToCurrent - 1)
        {
            return Cell.create(current.getX() + 1, current.getY());
        }
        if (current.getY() != 0 && distanceToCells[current.getX()][current.getY() - 1] == distanceToCurrent - 1)
        {
            return Cell.create(current.getX(), current.getY() - 1);
        }
        if (current.getY() != world.getHeight() - 1
                && distanceToCells[current.getX()][current.getY() + 1] == distanceToCurrent - 1)
        {
            return Cell.create(current.getX(), current.getY() + 1);
        }
        return null;
    }

    private List<Cell> findPathsFromDistanceTable(Cell start, Cell end, int[][] distanceToCells, World world)
    {
        List<Cell> path = new ArrayList<Cell>();
        for (Cell nextCell = end; nextCell != null; nextCell = findNextCellForPath(nextCell, distanceToCells, world))
        {
            path.add(nextCell);
        }
        Collections.reverse(path);
        return path;

    }

    private boolean isPassable(Cell cell, World world, boolean withTroopers)
    {
        if (withTroopers)
        {
            for (Trooper trooper : world.getTroopers())
            {
                if (trooper.getX() == cell.getX() && trooper.getY() == cell.getY())
                {
                    return false;
                }
            }
        }
        return CellType.FREE.equals(world.getCells()[cell.getX()][cell.getY()]);
    }
}

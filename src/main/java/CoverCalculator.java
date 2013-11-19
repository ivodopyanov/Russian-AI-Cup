/**
 * 
 */

import java.util.*;

import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 13 нояб. 2013 г.
 * 
 */
public class CoverCalculator
{

    public static final CoverCalculator INSTANCE = new CoverCalculator();

    public Cell findCover(Trooper self, List<Trooper> enemies, World world)
    {
        Set<Cell> visitedCells = new HashSet<Cell>();
        LinkedList<Cell> cellsToVisit = new LinkedList<Cell>();
        cellsToVisit.add(Cell.create(self.getX(), self.getY()));
        while (!cellsToVisit.isEmpty())
        {
            Cell cellToVisit = cellsToVisit.pop();
            if (!isVisibleForEnemies(cellToVisit, self, enemies, world))
            {
                return cellToVisit;
            }
            visitedCells.add(cellToVisit);
            cellsToVisit.addAll(findCellsToVisit(cellToVisit, world, visitedCells));
        }
        return null;// YOU'RE DOOMED, TROOPER!!! THERE IS NO ESCAPE!!!
    }

    private List<Cell> findCellsToVisit(Cell cell, World world, Set<Cell> visitedCells)
    {
        List<Cell> result = new ArrayList<Cell>();
        if (cell.getX() != 0)
        {
            Cell left = Cell.create(cell.getX() - 1, cell.getY());
            if (!visitedCells.contains(left))
            {
                result.add(left);
            }
        }
        if (cell.getX() != world.getWidth() - 1)
        {
            Cell right = Cell.create(cell.getX() + 1, cell.getY());
            if (!visitedCells.contains(right))
            {
                result.add(right);
            }
        }
        if (cell.getY() != 0)
        {
            Cell top = Cell.create(cell.getX(), cell.getY() - 1);
            if (!visitedCells.contains(top))
            {
                result.add(top);
            }
        }
        if (cell.getY() != world.getHeight() - 1)
        {
            Cell bottom = Cell.create(cell.getX(), cell.getY() + 1);
            if (!visitedCells.contains(bottom))
            {
                result.add(bottom);
            }
        }
        return result;
    }

    private boolean isVisibleForEnemies(Cell cell, Trooper self, List<Trooper> enemies, World world)
    {
        for (Trooper enemy : enemies)
        {
            if (world.isVisible(enemy.getShootingRange(), cell.getX(), cell.getY(), self.getStance(), enemy.getX(),
                    enemy.getY(), enemy.getStance()))
            {
                return true;
            }
        }
        return false;
    }

}

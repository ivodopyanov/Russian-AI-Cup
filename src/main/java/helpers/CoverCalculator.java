/**
 * 
 */
package helpers;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.Direction;
import model.Trooper;
import model.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author ivodopyanov
 * @since 13 нояб. 2013 г.
 *
 */
public class CoverCalculator
{
    public static final CoverCalculator INSTANCE = new CoverCalculator();

    public List<Direction> findCover(Trooper self, List<Trooper> enemies, World world)
    {
        Cell nearestCover = findCoverCell(self, enemies, world);
        return Helper.INSTANCE.getDirectionForPath(self.getX(), self.getY(), nearestCover.getX(), nearestCover.getY());
    }

    private List<Cell> findCellsToVisit(Cell cell, World world, Set<Cell> visitedCells)
    {
        List<Cell> result = Lists.newArrayList();
        if (cell.getX() != 0)
        {
            Cell left = new Cell(cell.getX() - 1, cell.getY());
            if (!visitedCells.contains(left))
            {
                result.add(left);
            }
        }
        if (cell.getX() != world.getWidth() - 1)
        {
            Cell right = new Cell(cell.getX() + 1, cell.getY());
            if (!visitedCells.contains(right))
            {
                result.add(right);
            }
        }
        if (cell.getY() != 0)
        {
            Cell top = new Cell(cell.getX(), cell.getY() - 1);
            if (!visitedCells.contains(top))
            {
                result.add(top);
            }
        }
        if (cell.getY() != world.getHeight() - 1)
        {
            Cell bottom = new Cell(cell.getX(), cell.getY() + 1);
            if (!visitedCells.contains(bottom))
            {
                result.add(bottom);
            }
        }
        return result;
    }

    private Cell findCoverCell(Trooper self, List<Trooper> enemies, World world)
    {
        Set<Cell> visitedCells = Sets.newHashSet();
        LinkedList<Cell> cellsToVisit = Lists.newLinkedList();
        cellsToVisit.add(new Cell(self.getX(), self.getY()));
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
        return null;//YOU'RE DOOMED, TROOPER!!! THERE IS NO ESCAPE!!!
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

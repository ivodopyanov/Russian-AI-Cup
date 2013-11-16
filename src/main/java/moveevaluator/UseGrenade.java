/**
 * 
 */
package moveevaluator;

import helpers.Cell;
import helpers.Helper;
import helpers.WeightFunctions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 *
 */
public class UseGrenade extends MoveEvaluatorImpl
{
    private static class GrenadeDamageEval
    {
        private final Cell cell;
        private final int damage;

        public GrenadeDamageEval(Cell cell, int damage)
        {
            this.cell = cell;
            this.damage = damage;
        }
    }

    private static final Comparator<GrenadeDamageEval> GRENADE_DAMAGE_COMPARATOR = new Comparator<GrenadeDamageEval>()
    {

        @Override
        public int compare(GrenadeDamageEval o1, GrenadeDamageEval o2)
        {
            return o1.damage - o2.damage;
        }
    };

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (game.getGrenadeThrowCost() > self.getActionPoints())
        {
            return;
        }
        List<Trooper> enemies = Helper.INSTANCE.findVisibleEnemies(self, world);
        List<GrenadeDamageEval> grenadeDamageEvals = calcGrenadeDamage(self, enemies, world, game);
        Collections.sort(grenadeDamageEvals, GRENADE_DAMAGE_COMPARATOR);
        if (grenadeDamageEvals.size() == 0)
        {
            return;
        }
        GrenadeDamageEval gde = grenadeDamageEvals.get(0);
        MoveEvaluation moveEvaluation = MoveEvaluation.throwGrenade(gde.cell.getX(), gde.cell.getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, WeightFunctions.throwGrenade(gde.damage));
    }

    private List<GrenadeDamageEval> calcGrenadeDamage(Trooper self, List<Trooper> enemies, World world, Game game)
    {
        List<GrenadeDamageEval> grenadeDamages = Lists.newArrayList();
        Set<Cell> visitedCells = Sets.newHashSet();
        for (Trooper enemy : enemies)
        {
            for (int x = enemy.getX() - 1; x <= enemy.getX() + 1; x++)
            {
                if (x == -1 || x == world.getWidth())
                {
                    continue;
                }
                for (int y = enemy.getY() - 1; y <= enemy.getY() + 1; y++)
                {
                    if (y == -1 || y == world.getHeight())
                    {
                        continue;
                    }
                    if (game.getGrenadeThrowRange() < distance(self.getX(), self.getY(), x, y))
                    {
                        continue;
                    }
                    Cell cell = new Cell(x, y);
                    if (visitedCells.contains(cell))
                    {
                        continue;
                    }
                    visitedCells.add(cell);
                    grenadeDamages.add(new GrenadeDamageEval(cell, getGrenadeDamage(cell, enemies, game)));
                }
            }
        }
        return grenadeDamages;
    }

    private double distance(int x1, int y1, int x2, int y2)
    {
        double deltax = x2 - x1;
        double deltay = y2 - y1;
        return Math.sqrt(deltax * deltax + deltay * deltay);
    }

    private int getGrenadeDamage(Cell cell, List<Trooper> enemies, Game game)
    {
        int result = 0;
        for (Trooper enemy : enemies)
        {
            if (enemy.getX() == cell.getX() && enemy.getY() == cell.getY())
            {
                result += game.getGrenadeDirectDamage();
            }
            if ((enemy.getY() == cell.getY() && Math.abs(enemy.getX() - cell.getX()) == 1)
                    || (enemy.getX() == cell.getX() && Math.abs(enemy.getY() - cell.getY()) == 1))
            {
                result += game.getGrenadeCollateralDamage();
            }
        }
        return result;
    }

}
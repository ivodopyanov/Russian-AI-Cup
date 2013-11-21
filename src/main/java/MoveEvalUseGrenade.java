/**
 * 
 */

import java.util.*;

import model.Game;
import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 *
 */
public class MoveEvalUseGrenade extends MoveEvalImpl
{

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (game.getGrenadeThrowCost() > self.getActionPoints() || !self.isHoldingGrenade())
        {
            return;
        }
        List<Trooper> enemies = Helper.INSTANCE.findVisibleEnemies(self, world, game.getGrenadeThrowRange());
        List<CombatCalculator.GrenadeDamageEval> grenadeDamageEvals = calcGrenadeDamage(self, enemies, world, game);
        Collections.sort(grenadeDamageEvals, CombatCalculator.GRENADE_DAMAGE_COMPARATOR);
        if (grenadeDamageEvals.size() == 0)
        {
            return;
        }
        CombatCalculator.GrenadeDamageEval gde = grenadeDamageEvals.get(0);
        MoveEvaluation moveEvaluation = MoveEvaluation.throwGrenade(gde.getCell().getX(), gde.getCell().getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, WeightFunctions.throwGrenade(gde.getDamage()));
    }

    private List<CombatCalculator.GrenadeDamageEval> calcGrenadeDamage(Trooper self, List<Trooper> enemies,
            World world, Game game)
    {
        List<CombatCalculator.GrenadeDamageEval> grenadeDamages = new ArrayList<CombatCalculator.GrenadeDamageEval>();
        Set<Cell> visitedCells = new HashSet<Cell>();
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
                    if (game.getGrenadeThrowRange() < Helper.INSTANCE.distance(self.getX(), self.getY(), x, y))
                    {
                        continue;
                    }
                    Cell cell = Cell.create(x, y);
                    if (visitedCells.contains(cell))
                    {
                        continue;
                    }
                    visitedCells.add(cell);
                    grenadeDamages.add(new CombatCalculator.GrenadeDamageEval(cell, CombatCalculator.INSTANCE
                            .getGrenadeDamage(cell, enemies, game)));
                }
            }
        }
        return grenadeDamages;
    }
}
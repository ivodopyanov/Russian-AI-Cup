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
        Collections.sort(grenadeDamageEvals, CombatCalculator.GRENADE_DAMAGE_COMPARATOR_WITHOUT_RESPOND_DAMAGE);
        if (grenadeDamageEvals.size() == 0)
        {
            return;
        }
        CombatCalculator.GrenadeDamageEval gde = grenadeDamageEvals.get(0);
        MoveEvaluation moveEvaluation = MoveEvaluation.throwGrenade(gde.getCell().getX(), gde.getCell().getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, WeightFunctions.throwGrenade(gde.getDamage()));
    }

    private int calcDamageFromShooting(Trooper self, List<Trooper> enemies, World world, Game game)
    {
        for (Trooper enemy : enemies)
        {
            if (world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(), enemy.getX(),
                    enemy.getY(), enemy.getStance()))
            {
                return self.getDamage() * self.getActionPoints() / self.getShootCost();
            }
        }
        return 0;
    }

    private List<CombatCalculator.GrenadeDamageEval> calcGrenadeDamage(Trooper self, List<Trooper> enemies,
            World world, Game game)
    {
        int damageFromShooting = calcDamageFromShooting(self, enemies, world, game);
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
                    int damageFromGrenade = CombatCalculator.INSTANCE.getGrenadeDamage(cell, enemies, game);
                    if (damageFromGrenade > damageFromShooting)
                    {
                        grenadeDamages.add(new CombatCalculator.GrenadeDamageEval(cell, damageFromGrenade,
                                CombatCalculator.INSTANCE.getEnemyDamage(cell, enemies, world, game)));
                    }
                }
            }
        }
        return grenadeDamages;
    }
}
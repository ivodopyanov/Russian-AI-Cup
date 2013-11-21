import java.util.*;

import model.Game;
import model.Trooper;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class MoveEvalMoveCloserToThrowGrenade extends MoveEvalImpl
{
    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (!self.isHoldingGrenade())
        {
            return;
        }

        List<Trooper> enemies = Helper.INSTANCE.findEnemies(world);

        List<CombatCalculator.GrenadeDamageEval> grenadeDamageEvals = calcGrenadeDamage(self, enemies, world, game);

        if (grenadeDamageEvals.size() == 0)
        {
            return;
        }
        Collections.sort(grenadeDamageEvals, CombatCalculator.GRENADE_DAMAGE_COMPARATOR_WITHOUT_RESPOND_DAMAGE);
        Cell cellWithinReach = findBestThrowCell(self, world, game, grenadeDamageEvals, 0, false);
        if (cellWithinReach != null)
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.move(cellWithinReach.getX(), cellWithinReach.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.GO_TO_THROW_GRENADE_POSITION);
        }
        Cell cellWithinReachWithRation = findBestThrowCell(self, world, game, grenadeDamageEvals, 0, true);
        if (cellWithinReachWithRation != null)
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.useFieldRation();
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.GO_TO_THROW_GRENADE_POSITION);
        }
        List<CombatCalculator.GrenadeDamageEval> grenadeDamageEvalsForRange = CombatCalculator.INSTANCE
                .filterGDEThreateningForLife(self, grenadeDamageEvals);
        Collections.sort(grenadeDamageEvalsForRange, CombatCalculator.GRENADE_DAMAGE_COMPARATOR_WITH_RESPOND_DAMAGE);
        if (grenadeDamageEvalsForRange.isEmpty())
        {
            return;
        }
        Cell cellWithin1Turn = findBestThrowCell(self, world, game, grenadeDamageEvalsForRange, 1, false);
        if (cellWithin1Turn != null)
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.move(cellWithin1Turn.getX(), cellWithin1Turn.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.GO_TO_THROW_GRENADE_POSITION);
        }
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

    private Cell findBestThrowCell(Trooper self, World world, Game game, List<CombatCalculator.GrenadeDamageEval> gdes,
            int turns, boolean withRation)
    {
        for (CombatCalculator.GrenadeDamageEval gde : gdes)
        {
            int distance = DistanceCalculator.INSTANCE.getDistance(Cell.create(self.getX(), self.getY()),
                    gde.getCell(), world, true);
            int apRequired = distance * game.getStandingMoveCost() + game.getGrenadeThrowCost();
            int apInStock = self.getActionPoints() + self.getInitialActionPoints() * turns;
            if (self.isHoldingFieldRation() && withRation && game.getFieldRationEatCost() < self.getActionPoints())
            {
                apInStock = game.getFieldRationBonusActionPoints() - game.getFieldRationEatCost();
            }
            if (apRequired <= apInStock)
            {
                return gde.getCell();
            }
        }
        return null;
    }
}

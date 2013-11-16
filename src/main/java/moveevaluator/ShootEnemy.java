/**
 * 
 */
package moveevaluator;

import helpers.Cell;
import helpers.Constants;
import helpers.CoverCalculator;
import helpers.Helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Direction;
import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

import com.google.common.collect.Collections2;

/**
 * @author ivodopyanov
 * @since 13 нояб. 2013 г.
 *
 */
public class ShootEnemy extends MoveEvaluatorImpl
{
    private static class EnemyPriorityComparator implements Comparator<Trooper>
    {

        private final Trooper self;

        public EnemyPriorityComparator(Trooper self)
        {
            this.self = self;
        }

        @Override
        public int compare(Trooper o1, Trooper o2)
        {
            return o1.getHitpoints() - o2.getHitpoints();
        }
    }

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        List<Trooper> reachableEnemies = Helper.INSTANCE.findVisibleEnemies(self, world);
        if (reachableEnemies.size() == 0 || self.getActionPoints() < self.getShootCost())
        {
            return;
        }
        Helper.TROOPER_DISTANCE_FILTER.reset(new Cell(self.getX(), self.getY()), 5);
        Collection<Trooper> teammatesNearby = Collections2.filter(Helper.INSTANCE.findSquad(world),
                Helper.TROOPER_DISTANCE_FILTER);
        boolean shouldRun = decideShootOrRun(reachableEnemies, teammatesNearby, self);
        if (shouldRun)
        {
            findCover(self, Helper.INSTANCE.findEnemies(world), world);
        }
        else
        {
            findTarget(self, reachableEnemies, world);
        }
    }

    private int calcMaxPossibleDamage(Collection<Trooper> threateningTroopers)
    {
        int result = 0;
        for (Trooper trooper : threateningTroopers)
        {
            result += trooper.getDamage();
        }
        return result;
    }

    private boolean decideShootOrRun(List<Trooper> reachableEnemies, Collection<Trooper> teammatesNearby, Trooper self)
    {
        int maxPossibleDamage = calcMaxPossibleDamage(reachableEnemies) / teammatesNearby.size() + 1;
        return maxPossibleDamage >= self.getHitpoints() || Constants.DEADLY_WOUNDED_HP >= self.getHitpoints();
    }

    private void findCover(Trooper self, List<Trooper> enemies, World world)
    {

        for (Direction direction : CoverCalculator.INSTANCE.findCover(self, enemies, world))
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.move(direction);
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.ESCAPE);
        }
    }

    private void findTarget(Trooper self, List<Trooper> reachableEnemies, World world)
    {
        Collections.sort(reachableEnemies, new EnemyPriorityComparator(self));
        MoveEvaluation result = MoveEvaluation.shoot(reachableEnemies.get(0).getX(), reachableEnemies.get(0).getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(result, Constants.SHOOT);
    }
}
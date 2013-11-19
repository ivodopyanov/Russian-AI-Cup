/**
 * 
 */

import java.util.*;

import model.Game;
import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 13 нояб. 2013 г.
 * 
 */
public class MoveEvalShootEnemy extends MoveEvalImpl
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
        Collection<Trooper> teammatesNearby = filterTeammatesNearby(Helper.INSTANCE.findSquad(world),
                Cell.create(self.getX(), self.getY()), 5, world);
        boolean shouldRun = decideShootOrRun(reachableEnemies, teammatesNearby, self);
        if (shouldRun)
        {
            Cell cover = CoverCalculator.INSTANCE.findCover(self, Helper.INSTANCE.findEnemies(world), world);
            MoveEvaluation moveEvaluation = MoveEvaluation.move(cover.getX(), cover.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.ESCAPE);
        }
        else
        {
            Collections.sort(reachableEnemies, new EnemyPriorityComparator(self));
            MoveEvaluation result = MoveEvaluation
                    .shoot(reachableEnemies.get(0).getX(), reachableEnemies.get(0).getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(result, Constants.SHOOT);
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

    //TODO: учитывать свои повреждения. Правильно рассчитать, кто кого быстрее убъет
    private boolean decideShootOrRun(List<Trooper> reachableEnemies, Collection<Trooper> teammatesNearby, Trooper self)
    {
        int maxPossibleDamage = calcMaxPossibleDamage(reachableEnemies) / teammatesNearby.size() + 1;
        return maxPossibleDamage >= self.getHitpoints() || Constants.DEADLY_WOUNDED_HP >= self.getHitpoints();
    }

    private List<Trooper> filterTeammatesNearby(List<Trooper> squad, Cell cell, int distance, World world)
    {
        List<Trooper> result = new ArrayList<Trooper>();
        for (Trooper trooper : squad)
        {
            if (DistanceCalculator.INSTANCE
                    .getDistance(cell, Cell.create(trooper.getX(), trooper.getY()), world, false) < distance)
            {
                result.add(trooper);
            }
        }
        return result;

    }
}
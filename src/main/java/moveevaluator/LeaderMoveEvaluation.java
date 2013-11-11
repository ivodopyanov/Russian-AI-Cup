/**
 * 
 */
package moveevaluator;

import helpers.*;

import java.util.Collections;
import java.util.List;

import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

import com.google.common.collect.Lists;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class LeaderMoveEvaluation extends MoveEvaluatorImpl
{
    public static final LeaderMoveEvaluation INSTANCE = new LeaderMoveEvaluation();

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        Trooper leader = Helper.INSTANCE.findSquadLeader(world);
        if (self.getId() != leader.getId())
        {
            return;
        }
        if (RadioChannel.INSTANCE.getLongTermPlan() == null)
        {
            thinkOutLongTermMoveStrategy(self, world, game);
        }
        MoveEvaluation moveEvaluation = MoveEvaluation
                .move(Helper.INSTANCE.getDirectionForPath(self.getX(), self.getY(), RadioChannel.INSTANCE
                        .getLongTermPlan().getX(), RadioChannel.INSTANCE.getLongTermPlan().getY()));
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.LONG_TERM_MOVE_EVALUATION);
    }

    private void thinkOutLongTermMoveStrategy(Trooper self, World world, Game game)
    {
        List<Cell> enemyLocations = Lists.newArrayList(RadioChannel.INSTANCE.getSpottedEnemies().keySet());
        DistanceCalculator.COMPARATOR.reset(new Cell(self.getX(), self.getY()));
        Collections.sort(enemyLocations, DistanceCalculator.COMPARATOR);
        RadioChannel.INSTANCE.setLongTermPlan(enemyLocations.get(0));
    }
}
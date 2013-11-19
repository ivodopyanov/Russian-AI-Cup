/**
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Game;
import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 * 
 */
public class MoveEvalLeaderMove extends MoveEvalImpl
{

    public static final MoveEvalLeaderMove INSTANCE = new MoveEvalLeaderMove();

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
        {
            return;
        }
        Trooper leader = Helper.INSTANCE.findSquadLeader(world);
        if (self.getId() != leader.getId())
        {
            return;
        }
        if (RadioChannel.INSTANCE.getLongTermPlan() == null)
        {
            thinkOutLongTermMoveStrategy(self, world, game);
        }
        List<Trooper> squad = Helper.INSTANCE.findSquad(world);
        squad.remove(self);
        if (DistanceCalculator.INSTANCE.getDistance(Helper.INSTANCE.centerOfTrooperGroup(squad),
                Cell.create(self.getX(), self.getY()), world, false) > Constants.MAX_TEAM_DISTANCE_FROM_LEADER)
        {
            //Не двигаемся вперед, если слишком далеко от остальной группы
            return;
        }
        MoveEvaluation moveEvaluation = MoveEvaluation.move(RadioChannel.INSTANCE.getLongTermPlan().getX(),
                RadioChannel.INSTANCE.getLongTermPlan().getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.LONG_TERM_MOVE_EVALUATION);
    }

    private void thinkOutLongTermMoveStrategy(Trooper self, World world, Game game)
    {
        List<Cell> enemyLocations = new ArrayList<Cell>(RadioChannel.INSTANCE.getSpottedEnemies().keySet());
        Collections.sort(enemyLocations,
                new DistanceCalculator.DistanceCellComparator(Cell.create(self.getX(), self.getY()), world, false));
        RadioChannel.INSTANCE.setLongTermPlan(enemyLocations.get(0));
    }
}

/**
 * 
 */
package moveevaluator;

import helpers.Cell;
import helpers.Constants;
import helpers.DistanceCalculator;
import helpers.Helper;
import helpers.RadioChannel;

import java.util.Collections;
import java.util.List;

import model.Direction;
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
public class LeaderMove extends MoveEvaluatorImpl
{
	
	public static final LeaderMove INSTANCE = new LeaderMove();
	
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
		for (Direction direction : Helper.INSTANCE.getDirectionForPath(self
		        .getX(), self.getY(), RadioChannel.INSTANCE.getLongTermPlan()
		        .getX(), RadioChannel.INSTANCE.getLongTermPlan().getY(), world))
		{
			MoveEvaluation moveEvaluation = MoveEvaluation.move(direction);
			MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation,
			        Constants.LONG_TERM_MOVE_EVALUATION);
		}
	}
	
	private void thinkOutLongTermMoveStrategy(Trooper self, World world, Game game)
	{
		List<Cell> enemyLocations = Lists.newArrayList(RadioChannel.INSTANCE
		        .getSpottedEnemies().keySet());
		Collections.sort(
		        enemyLocations,
		        new DistanceCalculator.DistanceCellComparator(new Cell(self
		                .getX(), self.getY()), world));
		RadioChannel.INSTANCE.setLongTermPlan(enemyLocations.get(0));
	}
}

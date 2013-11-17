/**
 * 
 */
package moveevaluator;

import helpers.Helper;
import helpers.WeightFunctions;

import java.util.List;

import model.Direction;
import model.Game;
import model.Trooper;
import model.TrooperType;
import model.World;
import trooperstrategy.MoveEvaluation;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 * 
 */
public class GoToWoundedTeammate extends MoveEvaluatorImpl
{
	
	@Override
	public void evaluate(Trooper self, World world, Game game)
	{
		if (!(TrooperType.FIELD_MEDIC.equals(self.getType()) || self
		        .isHoldingMedikit()))
		{
			return;// Боец не способен вылечить другого
		}
		if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
		{
			return;
		}
		List<Trooper> woundedTeammates = Helper.INSTANCE
		        .findWoundedTeammates(world);
		for (Trooper woundedTeammate : woundedTeammates)
		{
			for (Direction direction : Helper.INSTANCE.getDirectionForPath(
			        self.getX(), self.getY(), woundedTeammate.getX(),
			        woundedTeammate.getY(), world))
			{
				MoveEvaluation moveEvaluation = MoveEvaluation.move(direction);
				MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation,
				        WeightFunctions.teammateHealFunction(self,
				                woundedTeammate, world));
			}
		}
	}
}

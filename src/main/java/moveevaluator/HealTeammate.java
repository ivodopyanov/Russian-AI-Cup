/**
 * 
 */
package moveevaluator;

import helpers.Constants;
import helpers.DistanceCalculator;
import helpers.Helper;
import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 * 
 */
public class HealTeammate extends MoveEvaluatorImpl
{
	
	@Override
	public void evaluate(Trooper self, World world, Game game)
	{
		if (game.getFieldMedicHealCost() > self.getActionPoints())
		{
			return;
		}
		for (Trooper teammate : Helper.INSTANCE.findSquad(world))
		{
			if (DistanceCalculator.INSTANCE.getDistance(self.getX(),
			        self.getY(), teammate.getX(), teammate.getY(), world) <= 1
			        && teammate.getHitpoints() < teammate.getMaximalHitpoints())
			{
				MoveEvaluations.INSTANCE.addMoveEvaluation(
				        MoveEvaluation.heal(teammate.getX(), teammate.getY()),
				        Constants.HEAL);
			}
		}
	}
}

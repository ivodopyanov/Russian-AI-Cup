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
public class UseMedikit extends MoveEvaluatorImpl
{
    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (game.getMedikitUseCost() > self.getActionPoints())
        {
            return;
        }
        for (Trooper teammate : Helper.INSTANCE.findSquad(world))
        {
            if (DistanceCalculator.INSTANCE.getDistance(self.getX(), self.getY(), teammate.getX(), teammate.getY()) <= 1
                    && medikitIsUsedEfficiently(self, teammate, game))
            {
                MoveEvaluations.INSTANCE.addMoveEvaluation(MoveEvaluation.useMedikit(teammate.getX(), teammate.getY()),
                        Constants.HEAL);
            }
        }
    }

    private boolean medikitIsUsedEfficiently(Trooper self, Trooper teammate, Game game)
    {
        if (self.getId() == teammate.getId())
        {
            return teammate.getMaximalHitpoints() - teammate.getHitpoints() >= game.getMedikitHealSelfBonusHitpoints();
        }
        else
        {
            return teammate.getMaximalHitpoints() - teammate.getHitpoints() >= game.getMedikitBonusHitpoints();
        }
    }
}
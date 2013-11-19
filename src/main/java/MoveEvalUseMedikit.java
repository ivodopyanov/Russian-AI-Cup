/**
 * 
 */

import model.Game;
import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 * 
 */
public class MoveEvalUseMedikit extends MoveEvalImpl
{

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (game.getMedikitUseCost() > self.getActionPoints() || !self.isHoldingMedikit())
        {
            return;
        }
        for (Trooper teammate : Helper.INSTANCE.findSquad(world))
        {
            if (medikitIsUsedEfficiently(self, teammate, game)
                    && DistanceCalculator.INSTANCE.isNeighbourCell(self.getX(), self.getY(), teammate.getX(),
                            teammate.getY()))
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

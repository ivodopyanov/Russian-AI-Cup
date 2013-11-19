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
public class MoveEvalHealTeammate extends MoveEvalImpl
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
            if (teammate.getHitpoints() < teammate.getMaximalHitpoints()
                    && (DistanceCalculator.INSTANCE.isNeighbourCell(self.getX(), self.getY(), teammate.getX(),
                            teammate.getY()) || (self.getId() == teammate.getId())))
            {
                MoveEvaluations.INSTANCE.addMoveEvaluation(MoveEvaluation.heal(teammate.getX(), teammate.getY()),
                        Constants.HEAL);
            }
        }
    }
}

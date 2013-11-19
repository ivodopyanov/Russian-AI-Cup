/**
 * 
 */

import java.util.List;

import model.Game;
import model.Trooper;
import model.TrooperType;
import model.World;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 * 
 */
public class MoveEvalGoToWoundedTeammate extends MoveEvalImpl
{

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (!(TrooperType.FIELD_MEDIC.equals(self.getType()) || self.isHoldingMedikit()))
        {
            return;// Боец не способен вылечить другого
        }
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
        {
            return;
        }
        List<Trooper> woundedTeammates = Helper.INSTANCE.findWoundedTeammates(world);
        for (Trooper woundedTeammate : woundedTeammates)
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.move(woundedTeammate.getX(), woundedTeammate.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation,
                    WeightFunctions.teammateHealFunction(self, woundedTeammate, world));
        }
    }
}

/**
 * 
 */
package moveevaluator;

import helpers.Cell;
import helpers.Constants;
import helpers.FormationCalculator;
import helpers.Helper;
import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class KeepFormationOnLeader extends MoveEvaluatorImpl
{
    public static final KeepFormationOnLeader INSTANCE = new KeepFormationOnLeader();

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        Trooper leader = Helper.INSTANCE.findSquadLeader(world);
        if (self.getId() == leader.getId())
        {
            return;
        }
        Cell targetCell = FormationCalculator.INSTANCE.findTrooperPosition(self, leader, world);
        MoveEvaluation moveEvaluation = MoveEvaluation.move(Helper.INSTANCE.getDirectionForPath(self.getX(),
                self.getY(), targetCell.getX(), targetCell.getY()));
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.KEEP_FORMATION_MOVE_EVALUATION);
    }
}
/**
 * 
 */

import model.Game;
import model.Trooper;
import model.World;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 * 
 */
public class MoveEvalKeepFormationOnLeader extends MoveEvalImpl
{

    public static final MoveEvalKeepFormationOnLeader INSTANCE = new MoveEvalKeepFormationOnLeader();

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
        {
            return;
        }
        Trooper leader = Helper.INSTANCE.findSquadLeader(world);
        if (self.getId() == leader.getId())
        {
            return;
        }
        Cell targetCell = FormationCalculator.INSTANCE.findTrooperPosition(self, leader, world);
        if (DistanceCalculator.INSTANCE.getDistance(targetCell, Cell.create(self.getX(), self.getY()), world, false) > Constants.MAX_TEAM_DISTANCE_FROM_LEADER)
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.move(targetCell.getX(), targetCell.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.KEEP_FORMATION_MOVE_EVALUATION);
        }
    }
}

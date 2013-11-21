import java.util.LinkedList;
import java.util.List;

import model.Game;
import model.Trooper;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class MoveEvalEscapeFromFire extends MoveEvalImpl
{

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
        {
            return;
        }
        List<Trooper> enemies = Helper.INSTANCE.findVisibleEnemies(self, world, self.getVisionRange());
        if (enemies.isEmpty() && RadioChannel.INSTANCE.getSquadCondition().get(self.getId()).isBeingShot())
        {
            int moveCount = self.getActionPoints() / Helper.INSTANCE.getMoveCost(self, game);
            LinkedList<Cell> path = RadioChannel.INSTANCE.getTrooperPaths().get(self.getId());
            Cell escapeCell = path.get(path.size() - moveCount - 1);
            MoveEvaluation moveEvaluation = MoveEvaluation.move(escapeCell.getX(), escapeCell.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.ESCAPE);
        }
    }

}

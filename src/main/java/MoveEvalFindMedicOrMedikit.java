import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Game;
import model.Trooper;
import model.TrooperType;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class MoveEvalFindMedicOrMedikit extends MoveEvalImpl
{

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints()
                || self.getHitpoints() <= Constants.WOUNDED_HP || TrooperType.FIELD_MEDIC.equals(self.getType())
                || self.isHoldingMedikit())
        {
            return;
        }
        List<Trooper> troopers = findMedicOrTrooperWithMedikit(world);
        if (troopers.isEmpty())
        {
            return;
        }
        List<Cell> cells = extractCells(troopers);
        Collections.sort(cells, new DistanceCalculator.DistanceCellComparator(Cell.create(self.getX(), self.getY()),
                world, false));
        Cell goal = cells.get(0);
        if (DistanceCalculator.INSTANCE.isNeighbourCell(self.getX(), self.getY(), goal.getX(), goal.getY()))
        {
            MoveEvaluation moveEvaluation = MoveEvaluation.endTurn();
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.WAIT_FOR_MEDIC);
        }
        MoveEvaluation moveEvaluation = MoveEvaluation.move(cells.get(0).getX(), cells.get(0).getY());
        MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, Constants.ESCAPE);
    }

    private List<Cell> extractCells(List<Trooper> troopers)
    {
        List<Cell> result = new ArrayList<Cell>();
        for (Trooper trooper : troopers)
        {
            result.add(Cell.create(trooper.getX(), trooper.getY()));
        }
        return result;
    }

    private List<Trooper> findMedicOrTrooperWithMedikit(World world)
    {
        List<Trooper> result = new ArrayList<Trooper>();
        for (Trooper trooper : Helper.INSTANCE.findSquad(world))
        {
            if (TrooperType.FIELD_MEDIC.equals(trooper.getType()) || trooper.isHoldingMedikit())
            {
                result.add(trooper);
            }
        }
        return result;
    }

}

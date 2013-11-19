/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class MoveEvaluations
{
    public static final MoveEvaluations INSTANCE = new MoveEvaluations();

    private final List<MoveEvaluation> moveEvaluations = new ArrayList<MoveEvaluation>();

    public void addMoveEvaluation(MoveEvaluation moveEvaluation, double value)
    {
        int pos = moveEvaluations.indexOf(moveEvaluation);
        if (pos == -1)
        {
            moveEvaluations.add(moveEvaluation);
        }
        else
        {
            moveEvaluation = moveEvaluations.get(pos);
        }
        moveEvaluation.setEvaluation(moveEvaluation.getEvaluation() + value);
    }

    public List<MoveEvaluation> getMoveEvaluations()
    {
        return moveEvaluations;
    }

    public void reset()
    {
        moveEvaluations.clear();
    }
}
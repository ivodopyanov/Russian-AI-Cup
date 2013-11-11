package trooperstrategy;

import java.util.Collections;
import java.util.List;

import model.Game;
import model.Move;
import model.Trooper;
import model.World;
import moveevaluator.MoveEvaluations;
import moveevaluator.MoveEvaluator;

public class TrooperStrategyImpl implements TrooperStrategy
{

    private final List<MoveEvaluator> moveEvaluators;

    protected TrooperStrategyImpl(List<MoveEvaluator> moveEvaluators)
    {
        this.moveEvaluators = moveEvaluators;
    }

    @Override
    public void move(Trooper self, World world, Game game, Move move)
    {
        MoveEvaluations.INSTANCE.reset();
        for (MoveEvaluator moveEvaluator : moveEvaluators)
        {
            moveEvaluator.evaluate(self, world, game);
        }
        Collections.sort(MoveEvaluations.INSTANCE.getMoveEvaluations(), MoveEvaluation.MOVE_EVALUATION_COMPARATOR);
        MoveEvaluation bestMove = MoveEvaluations.INSTANCE.getMoveEvaluations().get(0);
        move.setAction(bestMove.getAction());
        move.setDirection(bestMove.getDirection());
        move.setX(bestMove.getX());
        move.setY(bestMove.getY());
    }
}

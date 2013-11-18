package trooperstrategy;

import java.util.Collections;
import java.util.List;

import model.*;
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
        assert self != null;
        assert self.getType() != null;
        System.console().printf("Trooper: type = %s, x = %d, y = %d\n", self.getType().toString(), self.getX(),
                self.getY());
        if (!MoveEvaluations.INSTANCE.getMoveEvaluations().isEmpty())
        {
            MoveEvaluation bestMove = MoveEvaluations.INSTANCE.getMoveEvaluations().get(0);
            move.setAction(bestMove.getAction());
            move.setDirection(bestMove.getDirection());
            move.setX(bestMove.getX());
            move.setY(bestMove.getY());
            System.console().printf(bestMove.toString());
        }
        else
        {
            move.setAction(ActionType.END_TURN);
            System.console().printf("End of turn\n");
        }

    }
}

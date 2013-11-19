import java.util.Collections;
import java.util.List;

import model.*;

public class StrategyTrooperImpl implements StrategyTrooper
{

    private final List<MoveEval> moveEvaluators;

    protected StrategyTrooperImpl(List<MoveEval> moveEvaluators)
    {
        this.moveEvaluators = moveEvaluators;
    }

    @Override
    public void move(Trooper self, World world, Game game, Move move)
    {
        MoveEvaluations.INSTANCE.reset();
        for (MoveEval moveEvaluator : moveEvaluators)
        {
            moveEvaluator.evaluate(self, world, game);
        }
        Collections.sort(MoveEvaluations.INSTANCE.getMoveEvaluations(), MoveEvaluation.MOVE_EVALUATION_COMPARATOR);
        if (!MoveEvaluations.INSTANCE.getMoveEvaluations().isEmpty())
        {
            MoveEvaluation bestMove = MoveEvaluations.INSTANCE.getMoveEvaluations().get(0);
            move.setAction(bestMove.getAction());
            if (ActionType.MOVE.equals(bestMove.getAction()))
            {
                Direction direction = getMoveDirection(Cell.create(self.getX(), self.getY()),
                        Cell.create(bestMove.getX(), bestMove.getY()), self, world, game);
                if (direction == null)
                {
                    move.setAction(ActionType.END_TURN);
                }
                else
                {
                    move.setDirection(direction);
                }
            }
            else
            {
                move.setX(bestMove.getX());
                move.setY(bestMove.getY());
            }
        }
        else
        {
            move.setAction(ActionType.END_TURN);
        }
    }

    private Direction getMoveDirection(Cell myCell, Cell goalCell, Trooper self, World world, Game game)
    {
        List<Cell> pathWithoutTroopers = DistanceCalculator.INSTANCE.getPath(myCell, goalCell, world, false);
        List<Cell> pathWithTroopers = DistanceCalculator.INSTANCE.getPath(myCell, goalCell, world, true);
        int stepsLeft = getStepsLeft(self, game);
        if (pathWithTroopers.size() < 2)//Пути с учетом бойцов нет вообще! Тогда незачем и шевелиться
        {
            return null;
        }
        Cell nextCell = pathWithTroopers.get(1);
        if (pathWithTroopers.size() > pathWithoutTroopers.size() + stepsLeft * 2)
        {
            return null;
        }
        return Helper.INSTANCE.getDirectionForNeighbours(myCell, nextCell);

    }

    private int getStepsLeft(Trooper self, Game game)
    {
        int moveCost;
        switch (self.getStance())
        {
        case KNEELING:
            moveCost = game.getKneelingMoveCost();
            break;
        case PRONE:
            moveCost = game.getProneMoveCost();
            break;
        default:
            moveCost = game.getStandingMoveCost();
            break;
        }
        return self.getActionPoints() / moveCost;
    }
}

package trooperstrategy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.Game;
import model.Move;
import model.Trooper;
import model.World;
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
		List<MoveEvaluation> moveEvaluations = new LinkedList<MoveEvaluation>();
		for (MoveEvaluator moveEvaluator : moveEvaluators)
		{
			moveEvaluator.evaluate(self, world, game, moveEvaluations);
		}
		Collections.sort(moveEvaluations,
		        MoveEvaluation.MOVE_EVALUATION_COMPARATOR);
		MoveEvaluation bestMove = moveEvaluations.get(0);
		move.setAction(bestMove.getAction());
		move.setDirection(bestMove.getDirection());
		move.setX(bestMove.getX());
		move.setY(bestMove.getY());
	}
}

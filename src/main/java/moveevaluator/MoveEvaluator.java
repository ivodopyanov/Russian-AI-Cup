package moveevaluator;

import java.util.List;

import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

public interface MoveEvaluator
{
	
	void evaluate(Trooper self, World world, Game game, List<MoveEvaluation> moveEvaluations);
}

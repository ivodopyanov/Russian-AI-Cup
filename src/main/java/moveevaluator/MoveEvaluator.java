package moveevaluator;

import model.Game;
import model.Trooper;
import model.World;

public interface MoveEvaluator
{

    void evaluate(Trooper self, World world, Game game);
}

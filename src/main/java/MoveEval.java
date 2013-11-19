

import model.Game;
import model.Trooper;
import model.World;

public interface MoveEval
{

    void evaluate(Trooper self, World world, Game game);
}

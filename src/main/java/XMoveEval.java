

import model.Game;
import model.Trooper;
import model.World;

public interface XMoveEval
{

    void evaluate(Trooper self, World world, Game game);
}

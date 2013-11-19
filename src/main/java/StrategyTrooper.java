

import model.Game;
import model.Move;
import model.Trooper;
import model.World;

public interface StrategyTrooper
{
	
	void move(Trooper self, World world, Game game, Move move);
}

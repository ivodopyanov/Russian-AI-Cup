import java.util.HashMap;
import java.util.Map;

import model.Game;
import model.Move;
import model.Trooper;
import model.TrooperType;
import model.World;
import trooperstrategy.CommanderStrategyImpl;
import trooperstrategy.MedicStrategyImpl;
import trooperstrategy.ScoutStrategyImpl;
import trooperstrategy.SniperStrategyImpl;
import trooperstrategy.SoldierStrategyImpl;
import trooperstrategy.TrooperStrategy;

public final class MyStrategy implements Strategy
{
	
	private static final Map<TrooperType, TrooperStrategy> TROOPER_STRATEGIES = new HashMap<TrooperType, TrooperStrategy>();
	static
	{
		TROOPER_STRATEGIES.put(TrooperType.COMMANDER,
		        new CommanderStrategyImpl());
		TROOPER_STRATEGIES
		        .put(TrooperType.FIELD_MEDIC, new MedicStrategyImpl());
		TROOPER_STRATEGIES.put(TrooperType.SCOUT, new ScoutStrategyImpl());
		TROOPER_STRATEGIES.put(TrooperType.SNIPER, new SniperStrategyImpl());
		TROOPER_STRATEGIES.put(TrooperType.SOLDIER, new SoldierStrategyImpl());
	}
	
	@Override
	public void move(Trooper self, World world, Game game, Move move)
	{
		TROOPER_STRATEGIES.get(self.getType()).move(self, world, game, move);
	}
}

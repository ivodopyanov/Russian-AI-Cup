import helpers.Cell;
import helpers.Helper;
import helpers.RadioChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Game;
import model.Move;
import model.Trooper;
import model.TrooperStance;
import model.TrooperType;
import model.World;
import trooperstrategy.CommanderStrategyImpl;
import trooperstrategy.MedicStrategyImpl;
import trooperstrategy.ScoutStrategyImpl;
import trooperstrategy.SniperStrategyImpl;
import trooperstrategy.SoldierStrategyImpl;
import trooperstrategy.TrooperStrategy;

import com.google.common.collect.Sets;

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
		if (!Helper.INSTANCE.isInitialized())
		{
			Helper.INSTANCE.init(self);
			RadioChannel.INSTANCE.init(world);
		}
		
		else if (Helper.INSTANCE.getFirstTrooperToMove().equals(self.getType()))
		{
			RadioChannel.INSTANCE.turnPassed();
		}
		scanSurroundings(self, world);
		TROOPER_STRATEGIES.get(self.getType()).move(self, world, game, move);
	}
	
	private void scanSurroundings(Trooper self, World world)
	{
		Set<Cell> newEnemyCells = Sets.newHashSet();
		for (Trooper trooper : world.getTroopers())
		{
			if (!trooper.isTeammate())
			{
				newEnemyCells.add(new Cell(trooper.getX(), trooper.getY()));
			}
		}
		Set<Cell> oldEnemyCells = RadioChannel.INSTANCE.getSpottedEnemies()
		        .keySet();
		Set<Cell> emptyCells = Sets.newHashSet();
		for (Cell oldEnemyCell : oldEnemyCells)
		{
			if (world.isVisible(self.getVisionRange(), self.getX(),
			        self.getY(), self.getStance(), oldEnemyCell.getX(),
			        oldEnemyCell.getY(), TrooperStance.STANDING)
			        && !newEnemyCells.contains(oldEnemyCell))
			{
				emptyCells.add(oldEnemyCell);
			}
		}
		for (Cell newEnemyCell : newEnemyCells)
		{
			RadioChannel.INSTANCE.enemySpotted(newEnemyCell);
		}
		for (Cell emptyCell : emptyCells)
		{
			RadioChannel.INSTANCE.enemyGone(emptyCell);
		}
	}
}

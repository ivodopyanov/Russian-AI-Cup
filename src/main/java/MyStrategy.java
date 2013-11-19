import java.util.*;

import model.*;

public final class MyStrategy implements Strategy
{

    private static final Map<TrooperType, StrategyTrooper> TROOPER_STRATEGIES = new HashMap<TrooperType, StrategyTrooper>();
    static
    {
        TROOPER_STRATEGIES.put(TrooperType.COMMANDER,
                new StrategyTrooperImpl(Arrays.<MoveEval> asList(Strategies.DEFAULT_MOVES)));
        TROOPER_STRATEGIES.put(TrooperType.FIELD_MEDIC,
                new StrategyTrooperImpl(Arrays.<MoveEval> asList(Strategies.MEDIC_MOVES)));
        TROOPER_STRATEGIES.put(TrooperType.SCOUT,
                new StrategyTrooperImpl(Arrays.<MoveEval> asList(Strategies.DEFAULT_MOVES)));
        TROOPER_STRATEGIES.put(TrooperType.SNIPER,
                new StrategyTrooperImpl(Arrays.<MoveEval> asList(Strategies.DEFAULT_MOVES)));
        TROOPER_STRATEGIES.put(TrooperType.SOLDIER,
                new StrategyTrooperImpl(Arrays.<MoveEval> asList(Strategies.DEFAULT_MOVES)));
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
        Set<Cell> newEnemyCells = new HashSet<Cell>();
        for (Trooper trooper : world.getTroopers())
        {
            if (!trooper.isTeammate())
            {
                newEnemyCells.add(Cell.create(trooper.getX(), trooper.getY()));
            }
        }
        Set<Cell> oldEnemyCells = RadioChannel.INSTANCE.getSpottedEnemies().keySet();
        Set<Cell> emptyCells = new HashSet<Cell>();
        for (Cell oldEnemyCell : oldEnemyCells)
        {
            if (world.isVisible(self.getVisionRange(), self.getX(), self.getY(), self.getStance(), oldEnemyCell.getX(),
                    oldEnemyCell.getY(), TrooperStance.STANDING) && !newEnemyCells.contains(oldEnemyCell))
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

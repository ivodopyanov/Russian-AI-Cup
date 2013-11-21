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
        if (RadioChannel.INSTANCE.getSquadCondition().get(self.getId()).getTurn() != world.getMoveIndex())
        {
            checkMyHealth(self, world);
            checkEnemyHealth(world);
        }
        scanSurroundings(self, world);
        updatePatrolPoints(self, world);
        TROOPER_STRATEGIES.get(self.getType()).move(self, world, game, move);
    }

    private void checkEnemyHealth(World world)
    {
        for (Trooper trooper : Helper.INSTANCE.findEnemies(world))
        {
            if (!RadioChannel.INSTANCE.getEnemyConditions().containsKey(trooper.getId()))
            {
                continue;
            }
            TrooperCondition condition = RadioChannel.INSTANCE.getEnemyConditions().get(trooper.getId());
            boolean isBeingHealed = condition.getTrooper().getHitpoints() < trooper.getHitpoints();
            condition.setBeingHealed(isBeingHealed);
        }
    }

    private void checkMyHealth(Trooper self, World world)
    {
        TrooperCondition condition = RadioChannel.INSTANCE.getSquadCondition().get(self.getId());
        boolean imBeingShot = self.getHitpoints() < condition.getTrooper().getHitpoints();
        condition.setBeingShot(imBeingShot);
        condition.setTrooper(self);
        condition.setTurn(world.getMoveIndex());

        Cell currentCell = Cell.create(self.getX(), self.getY());
        Cell prevCell = RadioChannel.INSTANCE.getTrooperPaths().get(self.getId()).peekLast();
        if (prevCell == null || !prevCell.equals(currentCell))
        {
            RadioChannel.INSTANCE.getTrooperPaths().get(self.getId()).add(currentCell);
        }
    }

    private void scanSurroundings(Trooper self, World world)
    {
        Map<Long, Trooper> newEnemies = new HashMap<Long, Trooper>();
        for (Trooper trooper : Helper.INSTANCE.findEnemies(world))
        {
            newEnemies.put(trooper.getId(), trooper);
        }
        Set<Long> goneEnemiesId = new HashSet<Long>();
        for (TrooperCondition oldEnemyCondition : RadioChannel.INSTANCE.getEnemyConditions().values())
        {
            if (world.isVisible(self.getVisionRange(), self.getX(), self.getY(), self.getStance(), oldEnemyCondition
                    .getTrooper().getX(), oldEnemyCondition.getTrooper().getY(), oldEnemyCondition.getTrooper()
                    .getStance())
                    && !newEnemies.containsKey(oldEnemyCondition.getTrooper().getId()))
            {
                goneEnemiesId.add(oldEnemyCondition.getTrooper().getId());
            }
        }
        for (Trooper newEnemy : newEnemies.values())
        {
            RadioChannel.INSTANCE.enemySpotted(newEnemy, world);
        }
        for (Long enemyGoneId : goneEnemiesId)
        {
            RadioChannel.INSTANCE.enemyGone(enemyGoneId);
        }
    }

    private void updatePatrolPoints(Trooper self, World world)
    {
        List<Cell> visiblePoints = new ArrayList<Cell>();
        for (Cell patrolPoint : RadioChannel.INSTANCE.getPatrolPoints())
        {
            if (world.isVisible(self.getVisionRange(), self.getX(), self.getY(), self.getStance(), patrolPoint.getX(),
                    patrolPoint.getY(), TrooperStance.PRONE))
            {
                visiblePoints.add(patrolPoint);
            }
        }
        for (Cell visiblePatrolPoint : visiblePoints)
        {
            RadioChannel.INSTANCE.getPatrolPoints().remove(visiblePoints);
            if (RadioChannel.INSTANCE.getLongTermPlan() != null
                    && RadioChannel.INSTANCE.getPatrolPoints().equals(visiblePatrolPoint))
            {
                RadioChannel.INSTANCE.setLongTermPlan(null);
            }
        }
    }
}

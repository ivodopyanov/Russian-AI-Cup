import java.util.*;
import java.util.Map.Entry;

import model.*;

public final class MyStrategy implements Strategy
{
    private static final List<OrderBuilder> ORDER_BUILDERS = Arrays.asList();

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
        updateMyPosition(self, world);
        scanSurroundingsForEnemies(self, world);
        scanSurroundingsForBonuses(world);
        updatePatrolPoints(self, world);
        if (world.getMoveIndex() == 1)
        {
            //Первый ход всегда пропускаем, чтобы определить очередность движения солдат
            RadioChannel.INSTANCE.getTurnOrder().add(self.getType());
            return;
        }
        if (RadioChannel.INSTANCE.doesRequireNewOrders())
        {
            rethinkOrders(self, world, game);
        }
        OrderForTurn currentOrderForTurn = RadioChannel.INSTANCE.getOrders().get(self.getId()).peek();
        Move nextMove = currentOrderForTurn.getOrders().poll();
        if (currentOrderForTurn.getOrders().isEmpty())
        {
            RadioChannel.INSTANCE.getOrders().get(self.getId()).poll();
        }
        move.setAction(nextMove.getAction());
        move.setDirection(nextMove.getDirection());
        move.setX(nextMove.getX());
        move.setY(nextMove.getY());
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
    }

    private void rethinkOrders(Trooper self, World world, Game game)
    {
        RadioChannel.INSTANCE.resetOrders();
        List<Trooper> squad = Helper.INSTANCE.findSquad(world);
        List<Bonus> visibleBonuses = Arrays.asList(world.getBonuses());
        List<Trooper> visibleEnemies = Helper.INSTANCE.findEnemies(world);
        for (OrderBuilder orderBuilder : ORDER_BUILDERS)
        {
            if (orderBuilder.isApplicable(squad, visibleBonuses, visibleEnemies, world, game))
            {
                orderBuilder.buildOrder(squad, visibleBonuses, visibleEnemies, world, game);
                break;
            }
        }
        RadioChannel.INSTANCE.ordersGiven();
    }

    private void scanSurroundingsForBonuses(World world)
    {
        Map<Cell, BonusType> visibleBonuses = new HashMap<Cell, BonusType>();
        for (Bonus bonus : world.getBonuses())
        {
            visibleBonuses.put(Cell.create(bonus.getX(), bonus.getY()), bonus.getType());
        }

        Set<Cell> goneBonuses = new HashSet<Cell>();
        for (Cell cell : RadioChannel.INSTANCE.getBonuses().keySet())
        {
            if (!visibleBonuses.containsKey(cell)
                    && Helper.INSTANCE.isVisibleForSquad(cell, world, TrooperStance.PRONE))
            {
                goneBonuses.add(cell);
            }
        }
        for (Cell goneBonus : goneBonuses)
        {
            RadioChannel.INSTANCE.bonusGone(goneBonus);
        }
        for (Entry<Cell, BonusType> visibleBonus : visibleBonuses.entrySet())
        {
            RadioChannel.INSTANCE.bonusSpotted(visibleBonus.getKey(), visibleBonus.getValue());
        }
    }

    private void scanSurroundingsForEnemies(Trooper self, World world)
    {
        Map<Long, Trooper> visibleEnemies = new HashMap<Long, Trooper>();
        for (Trooper trooper : Helper.INSTANCE.findEnemies(world))
        {
            visibleEnemies.put(trooper.getId(), trooper);
        }
        Set<Long> goneEnemiesId = new HashSet<Long>();
        for (TrooperCondition oldEnemyCondition : RadioChannel.INSTANCE.getEnemyConditions().values())
        {
            Cell oldTrooperCell = Cell.create(oldEnemyCondition.getTrooper().getX(), oldEnemyCondition.getTrooper()
                    .getY());
            if (Helper.INSTANCE.isVisibleForSquad(oldTrooperCell, world, oldEnemyCondition.getTrooper().getStance())
                    && !visibleEnemies.containsKey(oldEnemyCondition.getTrooper().getId()))
            {
                goneEnemiesId.add(oldEnemyCondition.getTrooper().getId());
            }
        }
        for (Trooper newEnemy : visibleEnemies.values())
        {
            RadioChannel.INSTANCE.enemySpotted(newEnemy, world);
        }
        for (Long enemyGoneId : goneEnemiesId)
        {
            RadioChannel.INSTANCE.enemyGone(enemyGoneId);
        }
    }

    private void updateMyPosition(Trooper self, World world)
    {
        Cell currentCell = Cell.create(self.getX(), self.getY());
        Cell prevCell = RadioChannel.INSTANCE.getTrooperPaths().get(self.getId()).peekLast();
        if (prevCell == null || !prevCell.equals(currentCell))
        {
            RadioChannel.INSTANCE.getTrooperPaths().get(self.getId()).add(currentCell);
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
                    && RadioChannel.INSTANCE.getLongTermPlan().equals(visiblePatrolPoint))
            {
                RadioChannel.INSTANCE.setLongTermPlan(null);
            }
        }
    }
}
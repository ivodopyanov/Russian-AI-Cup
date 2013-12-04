/**
 * 
 */

import java.util.*;

import model.*;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class RadioChannel
{
    public static final RadioChannel INSTANCE = new RadioChannel();

    private final Map<Long, TrooperCondition> squadConditions = new HashMap<Long, TrooperCondition>();
    private final Map<Long, TrooperCondition> enemyConditions = new HashMap<Long, TrooperCondition>();
    private final Map<Long, LinkedList<Cell>> trooperPaths = new HashMap<Long, LinkedList<Cell>>();
    private Cell longTermPlan;
    private final List<Cell> patrolPoints = new ArrayList<Cell>();
    private final Map<Long, OrderForTurn[]> orders = new HashMap<Long, OrderForTurn[]>();
    private final Map<Cell, BonusType> bonuses = new HashMap<Cell, BonusType>();
    private boolean requireNewOrders = true;
    private final List<TrooperType> turnOrder = new ArrayList<TrooperType>();

    public void bonusGone(Cell cell)
    {
        requireNewOrders |= true;
        bonuses.remove(cell);
    }

    public void bonusSpotted(Cell cell, BonusType bonusType)
    {
        requireNewOrders |= !bonuses.containsKey(cell);
        bonuses.put(cell, bonusType);
    }

    public boolean doesRequireNewOrders()
    {
        return requireNewOrders;
    }

    public void enemyGone(Long enemyId)
    {
        requireNewOrders |= true;
        enemyConditions.remove(enemyId);
    }

    public void enemySpotted(Trooper enemy, World world)
    {
        TrooperCondition condition;
        if (enemyConditions.containsKey(enemy.getId()))
        {
            condition = enemyConditions.get(enemy.getId());
            requireNewOrders |= condition.getTrooper().getX() != enemy.getX()
                    || condition.getTrooper().getY() != enemy.getY();
            condition.setTrooper(enemy);
        }
        else
        {
            condition = new TrooperCondition(enemy);
            enemyConditions.put(enemy.getId(), condition);
            requireNewOrders |= true;
        }
        condition.setTurn(world.getMoveIndex());
    }

    public Map<Cell, BonusType> getBonuses()
    {
        return bonuses;
    }

    public Map<Long, TrooperCondition> getEnemyConditions()
    {
        return enemyConditions;
    }

    public OrderForTurn getLastOrder(Long trooperId)
    {
        OrderForTurn[] ordersForTurn = orders.get(trooperId);
        for (int i = 0; i < ordersForTurn.length; i++)
        {
            if (ordersForTurn[ordersForTurn.length - i - 1] != null)
            {
                return ordersForTurn[ordersForTurn.length - i - 1];
            }
        }
        return null;
    }

    public Cell getLongTermPlan()
    {
        return longTermPlan;
    }

    public Map<Long, OrderForTurn[]> getOrders()
    {
        return orders;
    }

    public List<Cell> getPatrolPoints()
    {
        return patrolPoints;
    }

    public List<Cell> getPlannedSquadPosition(Trooper currentTrooper, int turnIndex, World world)
    {
        List<Cell> result = new ArrayList<Cell>();
        for (Trooper trooper : Helper.INSTANCE.findSquad(currentTrooper, world))
        {
            if (trooper.getId() == currentTrooper.getId())
            {
                continue;
            }
            boolean beginningOfTurn = turnOrder.indexOf(trooper.getType()) > turnOrder
                    .indexOf(currentTrooper.getType());
            Cell plannedTrooperPosition = getPlannedTrooperPosition(trooper.getId(), turnIndex, beginningOfTurn);
            if (plannedTrooperPosition != null)
            {
                result.add(plannedTrooperPosition);
            }
        }
        return result;
    }

    public Cell getPlannedTrooperPosition(Long trooperId, int turnIndex, boolean beginningOfTurn)
    {
        OrderForTurn orderForTurn = orders.get(trooperId)[turnIndex];
        if (orderForTurn == null)
        {
            return null;
        }
        return beginningOfTurn ? orderForTurn.getStart() : orderForTurn.getEnd();
    }

    public List<Cell> getSpottedEnemyCells()
    {
        List<Cell> result = new ArrayList<Cell>();
        for (TrooperCondition condition : enemyConditions.values())
        {
            result.add(Cell.create(condition.getTrooper().getX(), condition.getTrooper().getY()));
        }
        return result;
    }

    public Map<Long, TrooperCondition> getSquadCondition()
    {
        return squadConditions;
    }

    public Map<Long, LinkedList<Cell>> getTrooperPaths()
    {
        return trooperPaths;
    }

    public List<TrooperType> getTurnOrder()
    {
        return turnOrder;
    }

    public void giveMoveOrder(Long trooperId, List<PathNode> trooperPath)
    {
        if (trooperPath.isEmpty())
        {
            return;
        }
        int turnIndex = trooperPath.get(0).getTurnIndex();
        LinkedList<Move> movesForTurn = new LinkedList<Move>();
        LinkedList<PathNode> pathNodesForTurn = new LinkedList<PathNode>();
        for (PathNode pathNode : trooperPath)
        {
            if (pathNode.getTurnIndex() != turnIndex)
            {
                giveOrder(trooperId, new OrderForTurn(movesForTurn, turnIndex, pathNodesForTurn));
                turnIndex++;
                movesForTurn = new LinkedList<Move>();
                pathNodesForTurn = new LinkedList<PathNode>();
            }
            Move move = new Move();
            move.setAction(ActionType.MOVE);
            move.setX(pathNode.getCell().getX());
            move.setY(pathNode.getCell().getY());
            movesForTurn.add(move);
            pathNodesForTurn.add(pathNode);
        }
        OrderForTurn orderForTurn = new OrderForTurn(movesForTurn, turnIndex, pathNodesForTurn);
        giveOrder(trooperId, orderForTurn);
    }

    public void init(World world)
    {
        for (Trooper trooper : Helper.INSTANCE.findSquadWithoutOrdering(world))
        {
            TrooperCondition condition = new TrooperCondition(trooper);
            condition.setTurn(world.getMoveIndex());
            squadConditions.put(trooper.getId(), condition);

            LinkedList<Cell> trooperPath = new LinkedList<Cell>();
            trooperPath.add(Cell.create(trooper.getX(), trooper.getY()));
            trooperPaths.put(trooper.getId(), trooperPath);

            orders.put(trooper.getId(), new OrderForTurn[50]);
        }
    }

    public boolean isSomeoneBeingShot()
    {
        for (TrooperCondition teammateCondition : squadConditions.values())
        {
            if (teammateCondition.isBeingShot())
            {
                return true;
            }
        }
        return false;
    }

    public void ordersGiven()
    {
        requireNewOrders = false;
    }

    public void resetOrders(Trooper self, World world, List<Trooper> squad)
    {
        int turnOrderIndex = turnOrder.indexOf(self.getType());
        for (Long trooperId : orders.keySet())
        {
            OrderForTurn[] ordersForGame = orders.get(trooperId);
            if (trooperId != self.getId())
            {
                Trooper trooper = findTrooperById(squad, trooperId);
                if (trooper != null)
                {
                    int trooperTurnOrderIndex = turnOrder.indexOf(trooper.getType());
                    int modificator = turnOrderIndex < trooperTurnOrderIndex ? 1 : 0;
                    for (int i = world.getMoveIndex() + modificator; i < 50; i++)
                    {
                        ordersForGame[i] = null;
                    }
                }
            }
            else
            {
                int currentAP = self.getActionPoints();
                for (int i = world.getMoveIndex() + 1; i < 50; i++)
                {
                    ordersForGame[i] = null;
                }
                if (ordersForGame[world.getMoveIndex()] != null)
                {
                    PathNode lastPathNode = ordersForGame[world.getMoveIndex()].getPathNodes().peekLast();
                    while (lastPathNode.getCurrentAP() <= currentAP)
                    {
                        ordersForGame[world.getMoveIndex()].getPathNodes().pollLast();
                        lastPathNode = ordersForGame[world.getMoveIndex()].getPathNodes().peekLast();
                    }
                }
            }
        }
    }

    public void setLongTermPlan(Cell longTermPlan)
    {
        this.longTermPlan = longTermPlan;
    }

    private Trooper findTrooperById(List<Trooper> squad, Long trooperId)
    {
        for (Trooper trooper : squad)
        {
            if (trooper.getId() == trooperId)
            {
                return trooper;
            }
        }
        return null;
    }

    private void giveOrder(Long trooperId, OrderForTurn orderForTurn)
    {
        OrderForTurn[] orderForTurns = orders.get(trooperId);
        if (orderForTurns[orderForTurn.getTurnIndex()] == null)
        {
            orderForTurns[orderForTurn.getTurnIndex()] = orderForTurn;
        }
        else
        {
            orderForTurns[orderForTurn.getTurnIndex()].add(orderForTurn);
        }
    }
}
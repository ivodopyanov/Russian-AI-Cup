/**
 * 
 */

import java.util.*;

import model.Trooper;
import model.World;

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

    public void enemyGone(Long enemyId)
    {
        enemyConditions.remove(enemyId);
    }

    public void enemySpotted(Trooper enemy, World world)
    {
        TrooperCondition condition;
        if (enemyConditions.containsKey(enemy.getId()))
        {
            condition = enemyConditions.get(enemy.getId());
        }
        else
        {
            condition = new TrooperCondition();
            enemyConditions.put(enemy.getId(), condition);
        }
        condition.setTrooper(enemy);
        condition.setTurn(world.getMoveIndex());
    }

    public Map<Long, TrooperCondition> getEnemyConditions()
    {
        return enemyConditions;
    }

    public Cell getLongTermPlan()
    {
        return longTermPlan;
    }

    public List<Cell> getPatrolPoints()
    {
        return patrolPoints;
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

    public void init(World world)
    {
        for (Trooper trooper : Helper.INSTANCE.findSquad(world))
        {
            TrooperCondition condition = new TrooperCondition();
            condition.setTrooper(trooper);
            condition.setTurn(world.getMoveIndex());
            squadConditions.put(trooper.getId(), condition);

            LinkedList<Cell> trooperPath = new LinkedList<Cell>();
            trooperPath.add(Cell.create(trooper.getX(), trooper.getY()));
            trooperPaths.put(trooper.getId(), trooperPath);
        }
    }

    public void setLongTermPlan(Cell longTermPlan)
    {
        this.longTermPlan = longTermPlan;
    }
}
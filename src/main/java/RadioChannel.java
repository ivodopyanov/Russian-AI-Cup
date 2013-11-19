/**
 * 
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    //<Клетка где был замечен вражеский солдат, кол-во ходов прошедшее с того времени>
    private final Map<Cell, Integer> spottedEnemies = new HashMap<Cell, Integer>();

    private Cell longTermPlan;

    public void enemyGone(Cell cell)
    {
        spottedEnemies.remove(cell);
    }

    public void enemySpotted(Cell cell)
    {
        spottedEnemies.put(cell, 0);
    }

    public Cell getLongTermPlan()
    {
        return longTermPlan;
    }

    public Map<Cell, Integer> getSpottedEnemies()
    {
        return spottedEnemies;
    }

    public void init(World world)
    {
        for (Trooper trooper : Helper.INSTANCE.findSquad(world))
        {
            int x = world.getWidth() - 1 - trooper.getX();
            int y = world.getHeight() - 1 - trooper.getY();
            enemySpotted(Cell.create(trooper.getX(), y));
            enemySpotted(Cell.create(x, trooper.getY()));
            enemySpotted(Cell.create(x, y));
        }
    }

    public void setLongTermPlan(Cell longTermPlan)
    {
        this.longTermPlan = longTermPlan;
    }

    public void turnPassed()
    {
        for (Entry<Cell, Integer> entry : spottedEnemies.entrySet())
        {
            entry.setValue(entry.getValue() + 1);
        }
    }
}
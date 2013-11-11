/**
 * 
 */
package helpers;

import java.util.Map;
import java.util.Map.Entry;

import model.Trooper;
import model.World;

import com.google.common.collect.Maps;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class RadioChannel
{
    public static final RadioChannel INSTANCE = new RadioChannel();

    //<Клетка где был замечен вражеский солдат, кол-во ходов прошедшее с того времени>
    private final Map<Cell, Integer> spottedEnemies = Maps.newHashMap();

    private Cell longTermPlan;

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
            int x = world.getWidth() - trooper.getX();
            int y = world.getHeight() - trooper.getY();
            enemySpotted(new Cell(trooper.getX(), y));
            enemySpotted(new Cell(x, trooper.getY()));
            enemySpotted(new Cell(x, y));
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
/**
 * 
 */
package helpers;

import java.util.Map;

import model.Trooper;
import model.TrooperType;
import model.World;

import com.google.common.collect.Maps;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class FormationCalculator
{
    private static final Map<TrooperType, Cell> FORMATION = Maps.newHashMap();
    static
    {
        FORMATION.put(TrooperType.FIELD_MEDIC, new Cell(0, 1));
        FORMATION.put(TrooperType.SOLDIER, new Cell(1, 0));
        FORMATION.put(TrooperType.SCOUT, new Cell(-1, 0));
        FORMATION.put(TrooperType.SNIPER, new Cell(0, 2));
    }

    public static final FormationCalculator INSTANCE = new FormationCalculator();

    public Cell findTrooperPosition(Trooper trooper, Trooper leader, World world)
    {
        //Поправка на положение лидера т.о., чтобы формация всегда была направлена к центру
        int xMultiplier = leader.getX() < world.getWidth() / 2 ? -1 : 1;
        int yMultiplier = leader.getY() < world.getHeight() / 2 ? -1 : 1;

        Cell formationCell = FORMATION.get(trooper.getType());
        return Helper.INSTANCE.findPassableClosestCell(new Cell(leader.getX() + formationCell.getX() * xMultiplier,
                leader.getY() + formationCell.getY() * yMultiplier), world);
    }
}
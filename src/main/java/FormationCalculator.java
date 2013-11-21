/**
 * 
 */

import java.util.HashMap;
import java.util.Map;

import model.Trooper;
import model.TrooperType;
import model.World;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class FormationCalculator
{
    private static final Map<TrooperType, Cell> FORMATION = new HashMap<TrooperType, Cell>();
    static
    {
        FORMATION.put(TrooperType.COMMANDER, Cell.create(0, 1));
        FORMATION.put(TrooperType.FIELD_MEDIC, Cell.create(1, 0));
        FORMATION.put(TrooperType.SCOUT, Cell.create(-1, 0));
        FORMATION.put(TrooperType.SNIPER, Cell.create(0, 2));
    }

    public static final FormationCalculator INSTANCE = new FormationCalculator();

    public Cell findTrooperPosition(Trooper trooper, Trooper leader, World world)
    {
        //Поправка на положение лидера т.о., чтобы формация всегда была направлена к центру
        int xMultiplier = leader.getX() < world.getWidth() / 2 ? -1 : 1;
        int yMultiplier = leader.getY() < world.getHeight() / 2 ? -1 : 1;

        Cell formationCell = FORMATION.get(trooper.getType());
        return Helper.INSTANCE.findPassableClosestCell(
                Cell.create(leader.getX() + formationCell.getX() * xMultiplier, leader.getY() + formationCell.getY()
                        * yMultiplier), world);
    }
}
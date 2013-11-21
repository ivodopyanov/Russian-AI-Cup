import java.util.Comparator;
import java.util.List;

import model.Game;
import model.Trooper;
import model.TrooperType;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 20 нояб. 2013 г.
 *
 */
public class CombatCalculator
{
    public static class GrenadeDamageEval
    {
        private final Cell cell;
        private final int damage;

        public GrenadeDamageEval(Cell cell, int damage)
        {
            this.cell = cell;
            this.damage = damage;
        }

        public Cell getCell()
        {
            return cell;
        }

        public int getDamage()
        {
            return damage;
        }
    }

    public static final CombatCalculator INSTANCE = new CombatCalculator();

    public static final Comparator<GrenadeDamageEval> GRENADE_DAMAGE_COMPARATOR = new Comparator<GrenadeDamageEval>()
    {

        @Override
        public int compare(GrenadeDamageEval o1, GrenadeDamageEval o2)
        {
            return o1.damage - o2.damage;
        }
    };

    public int getGrenadeDamage(Cell cell, List<Trooper> enemies, Game game)
    {
        int result = 0;
        boolean medicAccounted = false;
        boolean someEnemyIsBeingHealed = false;
        for (Trooper enemy : enemies)
        {
            if (enemy.getX() == cell.getX() && enemy.getY() == cell.getY())
            {
                result += game.getGrenadeDirectDamage();
                someEnemyIsBeingHealed |= RadioChannel.INSTANCE.getEnemyConditions().get(enemy.getId()).isBeingHealed();
                medicAccounted |= TrooperType.FIELD_MEDIC.equals(enemy.getType());
            }
            if ((enemy.getY() == cell.getY() && Math.abs(enemy.getX() - cell.getX()) == 1)
                    || (enemy.getX() == cell.getX() && Math.abs(enemy.getY() - cell.getY()) == 1))
            {
                result += game.getGrenadeCollateralDamage();
                someEnemyIsBeingHealed |= RadioChannel.INSTANCE.getEnemyConditions().get(enemy.getId()).isBeingHealed();
                medicAccounted |= TrooperType.FIELD_MEDIC.equals(enemy.getType());
            }
        }
        if (!medicAccounted && someEnemyIsBeingHealed)
        {
            result += game.getGrenadeCollateralDamage();
        }
        return result;
    }
}

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.*;

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
        private final int respondDamage;

        public GrenadeDamageEval(Cell cell, int damage, int respondDamage)
        {
            this.cell = cell;
            this.damage = damage;
            this.respondDamage = respondDamage;
        }

        public Cell getCell()
        {
            return cell;
        }

        public int getDamage()
        {
            return damage;
        }

        public int getRespondDamage()
        {
            return respondDamage;
        }
    }

    public static final CombatCalculator INSTANCE = new CombatCalculator();

    public static final Comparator<GrenadeDamageEval> GRENADE_DAMAGE_COMPARATOR_WITH_RESPOND_DAMAGE = new Comparator<GrenadeDamageEval>()
    {

        @Override
        public int compare(GrenadeDamageEval o1, GrenadeDamageEval o2)
        {
            return (o1.damage - o2.damage) - (o2.respondDamage - o1.respondDamage);
        }
    };

    public static final Comparator<GrenadeDamageEval> GRENADE_DAMAGE_COMPARATOR_WITHOUT_RESPOND_DAMAGE = new Comparator<GrenadeDamageEval>()
    {

        @Override
        public int compare(GrenadeDamageEval o1, GrenadeDamageEval o2)
        {
            return (o1.damage - o2.damage);
        }
    };

    public List<GrenadeDamageEval> filterGDEThreateningForLife(Trooper self, List<GrenadeDamageEval> gdes)
    {
        List<GrenadeDamageEval> result = new ArrayList<GrenadeDamageEval>();
        for (GrenadeDamageEval gde : gdes)
        {
            if (self.getHitpoints() > gde.getRespondDamage())
            {
                result.add(gde);
            }
        }
        return result;
    }

    public int getEnemyDamage(Cell cell, List<Trooper> enemies, World world, Game game)
    {
        int result = 0;
        for (Trooper enemy : enemies)
        {
            if (world.isVisible(enemy.getShootingRange(), enemy.getX(), enemy.getY(), enemy.getStance(), cell.getX(),
                    cell.getY(), TrooperStance.STANDING))
            {
                result += enemy.getDamage() * enemy.getInitialActionPoints() / enemy.getShootCost();
            }
        }
        return result;
    }

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

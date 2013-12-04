import java.util.List;

import model.*;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 23 нояб. 2013 г.
 *
 */
public class OrderSelector
{
    public static final OrderSelector INSTANCE = new OrderSelector();

    public OrderType selectOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses,
            List<Trooper> visibleEnemies, World world, Game game)
    {
        if (!visibleEnemies.isEmpty())
        {
            return OrderType.ATTACK;
        }
        if (seePickableBonuses(squad, visibleBonuses) && visibleEnemies.isEmpty()
                && !RadioChannel.INSTANCE.isSomeoneBeingShot())
        {
            return OrderType.PICKUP_BONUSES;
        }
        if (squadIsTooFarFromEachOther(squad))
        {
            return OrderType.GROUP;
        }
        return OrderType.PATROL;
    }

    private boolean seePickableBonuses(List<Trooper> squad, List<Bonus> visibleBonuses)
    {
        for (Trooper teammate : squad)
        {
            for (Bonus visibleBonus : visibleBonuses)
            {
                if ((BonusType.FIELD_RATION.equals(visibleBonus.getType()) && !teammate.isHoldingFieldRation())
                        || (BonusType.GRENADE.equals(visibleBonus.getType()) && !teammate.isHoldingGrenade())
                        || (BonusType.MEDIKIT.equals(visibleBonus.getType()) && !teammate.isHoldingMedikit()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean squadIsTooFarFromEachOther(List<Trooper> squad)
    {
        int perimeter = 0;
        for (int i = 1; i < squad.size(); i++)
        {
            Trooper trooper1 = squad.get(i - 1);
            Trooper trooper2 = squad.get(i);
            perimeter += Helper.INSTANCE.distance(Cell.create(trooper1.getX(), trooper1.getY()),
                    Cell.create(trooper2.getX(), trooper2.getY()));
        }
        return perimeter <= 30;
    }
}

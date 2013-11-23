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
}

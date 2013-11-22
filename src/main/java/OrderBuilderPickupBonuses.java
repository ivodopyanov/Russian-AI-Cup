import java.util.*;
import java.util.Map.Entry;

import model.*;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class OrderBuilderPickupBonuses extends OrderBuilderImpl
{
    private static class BonusTrooperInfo
    {
        private final Bonus bonus;
        private final int distance;

        public BonusTrooperInfo(Bonus bonus, int distance)
        {
            this.bonus = bonus;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof BonusTrooperInfo))
            {
                return false;
            }
            BonusTrooperInfo bti = (BonusTrooperInfo)o;
            return getBonus().equals(bti.getBonus());
        }

        public Bonus getBonus()
        {
            return bonus;
        }

        public int getDistance()
        {
            return distance;
        }
    }

    private static final Comparator<BonusTrooperInfo> BONUS_TROOPER_INFO_COMPARATOR = new Comparator<BonusTrooperInfo>()
    {

        @Override
        public int compare(BonusTrooperInfo o1, BonusTrooperInfo o2)
        {
            return o1.getDistance() - o2.getDistance();
        }
    };

    public OrderBuilderPickupBonuses()
    {
        super(OrderType.PICKUP_BONUSES);
    }

    @Override
    public void buildOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game)
    {
        Map<Trooper, List<BonusTrooperInfo>> availableBonuses = calcAvailableBonuses(squad, visibleBonuses, world);
        Map<Trooper, List<Bonus>> distributedBonuses = distributeBonuses(availableBonuses);
        buildOrders(distributedBonuses, world);
    }

    @Override
    public boolean isApplicable(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses,
            List<Trooper> visibleEnemies, World world, Game game)
    {
        return seePickableBonuses(squad, visibleBonuses) && visibleEnemies.isEmpty()
                && !RadioChannel.INSTANCE.isSomeoneBeingShot();
    }

    private void buildOrders(Map<Trooper, List<Bonus>> bonuses, World world)
    {
        for (Entry<Trooper, List<Bonus>> entry : bonuses.entrySet())
        {
            Cell start = Cell.create(entry.getKey().getX(), entry.getKey().getY());
            Cell end = null;
            for (Bonus bonus : entry.getValue())
            {
                end = Cell.create(bonus.getX(), bonus.getY());
                List<Cell> path = DistanceCalculator.INSTANCE.getPath(start, end, world, false);
                RadioChannel.INSTANCE.giveMoveOrder(entry.getKey().getId(), path);
            }
        }
    }

    private Map<Trooper, List<BonusTrooperInfo>> calcAvailableBonuses(List<Trooper> squad, List<Bonus> visibleBonuses,
            World world)
    {
        Map<Trooper, List<BonusTrooperInfo>> result = new HashMap<Trooper, List<BonusTrooperInfo>>();
        for (Trooper teammate : squad)
        {
            Cell trooperCell = Cell.create(teammate.getX(), teammate.getY());
            List<BonusTrooperInfo> pickableBonuses = new ArrayList<BonusTrooperInfo>();
            for (Bonus visibleBonus : visibleBonuses)
            {
                if ((BonusType.FIELD_RATION.equals(visibleBonus.getType()) && !teammate.isHoldingFieldRation())
                        || (BonusType.GRENADE.equals(visibleBonus.getType()) && !teammate.isHoldingGrenade())
                        || (BonusType.MEDIKIT.equals(visibleBonus.getType()) && !teammate.isHoldingMedikit()))
                {
                    pickableBonuses.add(new BonusTrooperInfo(visibleBonus, (int)Helper.INSTANCE.distance(trooperCell,
                            Cell.create(visibleBonus.getX(), visibleBonus.getY()))));
                }
            }
            Collections.sort(pickableBonuses, BONUS_TROOPER_INFO_COMPARATOR);
            result.put(teammate, pickableBonuses);
        }
        return result;
    }

    private Map<Trooper, List<Bonus>> distributeBonuses(Map<Trooper, List<BonusTrooperInfo>> avaiableBonuses)
    {
        Map<Trooper, List<Bonus>> result = new HashMap<Trooper, List<Bonus>>();
        for (Trooper trooper : avaiableBonuses.keySet())
        {
            result.put(trooper, new ArrayList<Bonus>());
        }
        Trooper luckyTrooper = findTrooperWithClosestBonus(avaiableBonuses);
        while (luckyTrooper != null)
        {
            BonusTrooperInfo bonus = avaiableBonuses.get(luckyTrooper).get(0);
            result.get(luckyTrooper).add(bonus.getBonus());
            for (Trooper trooper : avaiableBonuses.keySet())
            {
                avaiableBonuses.get(trooper).remove(bonus);
            }
            luckyTrooper = findTrooperWithClosestBonus(avaiableBonuses);
        }
        return result;
    }

    private Trooper findTrooperWithClosestBonus(Map<Trooper, List<BonusTrooperInfo>> avaiableBonuses)
    {
        Trooper result = null;
        int distance = 999;
        for (Entry<Trooper, List<BonusTrooperInfo>> entry : avaiableBonuses.entrySet())
        {
            if (entry.getValue().isEmpty())
            {
                continue;
            }
            if (entry.getValue().get(0).getDistance() < distance)
            {
                distance = entry.getValue().get(0).getDistance();
                result = entry.getKey();
            }
        }
        return result;
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
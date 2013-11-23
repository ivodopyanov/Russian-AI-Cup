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
        List<Trooper> troopers = new ArrayList<Trooper>(squad);
        Collections.sort(troopers, new OrderEdictionSorter(self.getType()));
        Map<Trooper, List<BonusTrooperInfo>> availableBonuses = calcAvailableBonuses(troopers, visibleBonuses, world);
        Map<Trooper, List<Bonus>> distributedBonuses = distributeBonuses(availableBonuses);
        Map<Trooper, PathNode> endingPathNodes = buildBonusCollectionOrders(self, troopers, distributedBonuses, world,
                game);
        buildConvergenceOrders(endingPathNodes, troopers, self, world, game);

    }

    private Map<Trooper, PathNode> buildBonusCollectionOrders(Trooper self, List<Trooper> troopers,
            Map<Trooper, List<Bonus>> bonuses, World world, Game game)
    {
        Map<Trooper, PathNode> endingPathNodes = new HashMap<Trooper, PathNode>();
        for (Trooper trooper : troopers)
        {
            int turnIndex = world.getMoveIndex();
            int ap = trooper.getInitialActionPoints();
            if (self.getId() == trooper.getId())
            {
                ap = self.getActionPoints();
            }
            else if (RadioChannel.INSTANCE.getTurnOrder().indexOf(trooper.getType()) < RadioChannel.INSTANCE
                    .getTurnOrder().indexOf(self.getType()))
            {
                turnIndex++;
                ap = trooper.getInitialActionPoints();
            }
            if (!TrooperType.COMMANDER.equals(trooper.getType())
                    && trooper.getId() != self.getId()
                    && DistanceCalculator.INSTANCE.isCommanderNearby(Cell.create(trooper.getX(), trooper.getY()),
                            trooper, turnIndex, world))
            {
                ap += game.getCommanderAuraBonusActionPoints();
            }
            PathNode lastPathNode = new PathNode(Cell.create(trooper.getX(), trooper.getY()), turnIndex, null, ap, 0,
                    trooper);
            for (Bonus bonus : bonuses.get(trooper))
            {
                Cell end = Cell.create(bonus.getX(), bonus.getY());
                LinkedList<PathNode> path = DistanceCalculator.INSTANCE.getPath(new DistanceCalcContext(trooper,
                        lastPathNode.getCurrentAP(), lastPathNode.getTurnIndex(), lastPathNode.getCell()), end, world,
                        game);
                RadioChannel.INSTANCE.giveMoveOrder(trooper.getId(), path);
                lastPathNode = path.peekLast();

            }
            endingPathNodes.put(trooper, lastPathNode);
        }
        return endingPathNodes;
    }

    private void buildConvergenceOrders(Map<Trooper, PathNode> startPathNodes, List<Trooper> squad, Trooper self,
            World world, Game game)
    {
        List<DistanceCalcContext> contexts = new ArrayList<DistanceCalcContext>();
        for (Trooper trooper : squad)
        {
            PathNode startPathNode = startPathNodes.get(trooper);
            contexts.add(new DistanceCalcContext(trooper, startPathNode.getCurrentAP(), startPathNode.getTurnIndex(),
                    startPathNode.getCell()));
        }
        Map<Trooper, List<PathNode>> convergencePaths = DistanceConvergenceCalculator.INSTANCE.getConvergencePaths(
                self, contexts, world, game);
        for (Entry<Trooper, List<PathNode>> entry : convergencePaths.entrySet())
        {
            RadioChannel.INSTANCE.giveMoveOrder(entry.getKey().getId(), entry.getValue());
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
                    pickableBonuses.add(new BonusTrooperInfo(visibleBonus, Helper.INSTANCE.distance(trooperCell,
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
}
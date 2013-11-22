import java.util.*;

import model.*;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class OrderBuilderPatrolArea extends OrderBuilderImpl
{

    private static class PatrolCellComparator implements Comparator<Cell>
    {
        private final Trooper trooper;

        public PatrolCellComparator(Trooper trooper)
        {
            this.trooper = trooper;
        }

        @Override
        public int compare(Cell o1, Cell o2)
        {
            int index1 = RadioChannel.INSTANCE.getTrooperPaths().get(trooper.getId()).indexOf(o1);
            int index2 = RadioChannel.INSTANCE.getTrooperPaths().get(trooper.getId()).indexOf(o2);
            if (index1 == -1 && index2 != -1)
            {
                return -1;
            }
            if (index1 != -1 && index2 == -1)
            {
                return 1;
            }
            if (index1 != -1 && index2 != -1)
            {
                return index1 - index2;
            }
            Cell trooperCell = Cell.create(trooper.getX(), trooper.getY());
            int distance1 = (int)Helper.INSTANCE.distance(trooperCell, o1);
            int distance2 = (int)Helper.INSTANCE.distance(trooperCell, o2);
            return distance2 - distance1;
        }

    }

    public OrderBuilderPatrolArea()
    {
        super(OrderType.PATROL);
    }

    @Override
    public void buildOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game)
    {
        Map<Trooper, Cell> goals = getNextCells(self, world);
        List<Trooper> troopers = new ArrayList<Trooper>(squad);
        Collections.sort(troopers, new OrderEdictionSorter(self.getType()));
        for (Trooper trooper : troopers)
        {
            Cell trooperCell = Cell.create(trooper.getX(), trooper.getY());
            int ap = trooper.getActionPoints();
            int turnIndex = world.getMoveIndex();
            if (RadioChannel.INSTANCE.getTurnOrder().indexOf(trooper.getType()) < RadioChannel.INSTANCE.getTurnOrder()
                    .indexOf(self.getType()))
            {
                turnIndex++;
                ap = trooper.getInitialActionPoints();
                if (DistanceCalculator.INSTANCE.isCommanderNearby(Cell.create(trooper.getX(), trooper.getY()), trooper,
                        turnIndex, world))
                {
                    ap += game.getCommanderAuraBonusActionPoints();
                }
            }
            List<PathNode> path = DistanceCalculator.INSTANCE.getPath(trooper, ap, turnIndex, trooperCell,
                    goals.get(trooper), world, game);
            RadioChannel.INSTANCE.giveMoveOrder(trooper.getId(), path);
        }
    }

    @Override
    public boolean isApplicable(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses,
            List<Trooper> visibleEnemies, World world, Game game)
    {
        return visibleBonuses.size() == 0 && visibleEnemies.size() == 0 && !RadioChannel.INSTANCE.isSomeoneBeingShot();
    }

    private Map<Trooper, Cell> getNextCells(Trooper self, World world)
    {
        Map<Trooper, Cell> result = new HashMap<Trooper, Cell>();
        List<TrooperType> turnOrder = RadioChannel.INSTANCE.getTurnOrder();
        int selfTurnOrderIndex = turnOrder.indexOf(self.getType());
        for (int i = 0; i < turnOrder.size(); i++)
        {
            //Пробегаемся поочередно по всем живым соратникам в том порядке, в каком они будут ходить, начиная с текущего
            Trooper teammate = Helper.INSTANCE.findTeammateTrooperByType(world,
                    turnOrder.get((i + selfTurnOrderIndex) % turnOrder.size()));
            if (teammate == null)
            {
                continue;
            }
            Cell originalCell = RadioChannel.INSTANCE.getTrooperPaths().get(teammate.getId()).get(0);
            List<Cell> patrolPoints = new ArrayList<Cell>();
            patrolPoints.add(Cell.create(originalCell.getX(), world.getHeight() - originalCell.getY() - 1));
            patrolPoints.add(Cell.create(world.getWidth() - originalCell.getX() - 1,
                    world.getHeight() - originalCell.getY() - 1));
            patrolPoints.add(Cell.create(world.getWidth() - originalCell.getX() - 1,
                    world.getHeight() - originalCell.getY() - 1));
            Collections.sort(patrolPoints, new PatrolCellComparator(teammate));
            result.put(teammate, patrolPoints.get(0));
        }
        return result;
    }
}
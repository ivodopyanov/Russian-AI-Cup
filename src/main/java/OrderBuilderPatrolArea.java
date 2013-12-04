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
            int distance1 = Helper.INSTANCE.distance(trooperCell, o1);
            int distance2 = Helper.INSTANCE.distance(trooperCell, o2);
            return distance2 - distance1;
        }

    }

    @Override
    public void buildOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game)
    {
        Map<Trooper, Cell> goals = getNextCells(self, squad, world);
        Map<Trooper, DistanceCalcContext> contexts = getDistanceCalcContexts(squad, self, world, game);
        for (Trooper trooper : squad)
        {
            List<PathNode> path = DistanceCalculator.INSTANCE.getPath(contexts.get(trooper), goals.get(trooper), world,
                    game);
            RadioChannel.INSTANCE.giveMoveOrder(trooper.getId(), path);
        }
    }

    protected Map<Trooper, DistanceCalcContext> getDistanceCalcContexts(List<Trooper> squad, Trooper self, World world,
            Game game)
    {
        Map<Trooper, DistanceCalcContext> result = new HashMap<Trooper, DistanceCalcContext>();
        for (Trooper trooper : squad)
        {
            Cell trooperCell = Cell.create(trooper.getX(), trooper.getY());
            int ap = trooper.getInitialActionPoints();
            int turnIndex = world.getMoveIndex();
            if (trooper.getId() == self.getId())
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
            result.put(trooper, new DistanceCalcContext(trooper, ap, turnIndex, trooperCell));
        }

        return result;
    }

    protected Map<Trooper, Cell> getNextCells(Trooper self, List<Trooper> squad, World world)
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
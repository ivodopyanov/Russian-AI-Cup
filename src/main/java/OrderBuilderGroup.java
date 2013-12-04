import java.util.*;

import model.Bonus;
import model.Game;
import model.Trooper;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 25 нояб. 2013 г.
 *
 */
public class OrderBuilderGroup extends OrderBuilderPatrolArea
{
    private static final class PathNodeTurnComparator implements Comparator<PathNode>
    {
        private final PathNode goal;

        public PathNodeTurnComparator(PathNode goal)
        {
            this.goal = goal;
        }

        @Override
        public int compare(PathNode o1, PathNode o2)
        {
            return func(o1) - func(o2);
        }

        private int func(PathNode pathNode)
        {
            return Helper.INSTANCE.distance(pathNode.getCell(), goal.getCell()) + goal.getSpentAP()
                    - pathNode.getSpentAP();
        }

    }

    @Override
    public void buildOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game)
    {

    }

    @Override
    protected Map<Trooper, DistanceCalcContext> getDistanceCalcContexts(List<Trooper> squad, Trooper self, World world,
            Game game)
    {
        Map<Trooper, DistanceCalcContext> result = new HashMap<Trooper, DistanceCalcContext>();
        for (Trooper trooper : squad)
        {
            OrderForTurn orderForTurn = RadioChannel.INSTANCE.getLastOrder(trooper.getId());
            PathNode lastPathNode = orderForTurn.getPathNodes().peekLast();
            result.put(trooper,
                    new DistanceCalcContext(trooper, lastPathNode.getCurrentAP(), lastPathNode.getTurnIndex(),
                            lastPathNode.getCell()));
        }
        return result;
    }

    @Override
    protected Map<Trooper, Cell> getNextCells(Trooper self, List<Trooper> squad, World world)
    {
        Trooper leader = Helper.INSTANCE.findSquadLeader(world);
        List<PathNode> startPathNodes = new ArrayList<PathNode>();
        PathNode leaderStartPathNode = null;
        for (Trooper trooper : squad)
        {
            OrderForTurn orderForTurn = RadioChannel.INSTANCE.getLastOrder(trooper.getId());
            startPathNodes.add(orderForTurn.getPathNodes().peekLast());
            if (trooper.getId() == leader.getId())
            {
                leaderStartPathNode = orderForTurn.getPathNodes().peekLast();
            }
        }
        Collections.sort(startPathNodes, new PathNodeTurnComparator(leaderStartPathNode));
        Cell goal = startPathNodes.get(startPathNodes.size() - 1).getCell();
        Map<Trooper, Cell> result = new HashMap<Trooper, Cell>();
        for (Trooper trooper : squad)
        {
            result.put(trooper, goal);
        }
        return result;
    }
}

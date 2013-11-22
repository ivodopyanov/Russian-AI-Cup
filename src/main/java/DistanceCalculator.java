/**
 * 
 */

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import model.*;

/**
 * @author ivodopyanov
 * @since 11 ﾐｽﾐｾﾑ紹ｱ. 2013 ﾐｳ.
 * 
 */
public class DistanceCalculator
{
    public static class DistanceCellComparator implements Comparator<Cell>
    {

        private final Cell pivot;
        private final World world;
        private final Trooper trooper;
        private final Game game;
        private final int startAP;

        public DistanceCellComparator(Trooper trooper, int startAP, Cell pivot, World world, Game game)
        {
            this.pivot = pivot;
            this.world = world;
            this.trooper = trooper;
            this.game = game;
            this.startAP = startAP;
        }

        @Override
        public int compare(Cell o1, Cell o2)
        {
            return INSTANCE.getDistance(trooper, startAP, pivot, o1, world, game)
                    - INSTANCE.getDistance(trooper, startAP, pivot, o2, world, game);
        }
    }

    public static final Comparator<PathNode> PATH_NODE_COMPARATOR = new Comparator<PathNode>()
    {
        @Override
        public int compare(PathNode o1, PathNode o2)
        {
            return o1.getSpentAP() - o2.getSpentAP();
        }
    };

    public static final DistanceCalculator INSTANCE = new DistanceCalculator();

    public int getDistance(Trooper trooper, int startAP, Cell start, Cell end, World world, Game game)
    {
        PathNode startNode = new PathNode(start, world.getMoveIndex(), null, trooper.getActionPoints(), 0);
        TreeSet<PathNode> allNodes = new TreeSet<PathNode>(PATH_NODE_COMPARATOR);
        PathNode endingNode = calcDistanceRecurs2(trooper, start, end, startNode, allNodes, world, game);
        return endingNode.getSpentAP();
    }

    public List<PathNode> getPath(Trooper trooper, int startAP, int turnIndex, Cell start, Cell end, World world,
            Game game)
    {
        PathNode startNode = new PathNode(start, turnIndex, null, trooper.getActionPoints(), 0);
        TreeSet<PathNode> allNodes = new TreeSet<PathNode>(PATH_NODE_COMPARATOR);
        PathNode endingNode = calcDistanceRecurs2(trooper, start, end, startNode, allNodes, world, game);
        LinkedList<PathNode> result = new LinkedList<PathNode>();
        while (endingNode != null)
        {
            result.push(endingNode);
            endingNode = endingNode.getPrevPathNode();
        }
        return result;
    }

    public boolean isCommanderNearby(Cell cell, Trooper trooper, int turnIndex, World world)
    {
        Trooper commander = Helper.INSTANCE.findTeammateTrooperByType(world, TrooperType.COMMANDER);
        if (commander == null)
        {
            return false;//Командира с нами больше нет!
        }
        boolean beginningOfTurn = RadioChannel.INSTANCE.getTurnOrder().indexOf(TrooperType.COMMANDER) > RadioChannel.INSTANCE
                .getTurnOrder().indexOf(trooper.getType());
        Cell commanderPosition = RadioChannel.INSTANCE.getPlannedTrooperPosition(commander.getId(), turnIndex,
                beginningOfTurn);
        return this.isNeighbourCell(cell.getX(), cell.getY(), commanderPosition.getX(), commanderPosition.getY());
    }

    public boolean isNeighbourCell(int x1, int y1, int x2, int y2)
    {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    private PathNode calcDistanceRecurs2(Trooper trooper, Cell start, Cell end, PathNode currentNode,
            TreeSet<PathNode> allNodes, World world, Game game)
    {
        if (currentNode == null)
        {
            return null;//Путь недостижим в принципе
        }
        if (currentNode.getCell().equals(end))
        {
            return currentNode;
        }
        boolean nextTurn = currentNode.getCurrentAP() < game.getStandingMoveCost();
        int turnIndex = currentNode.getTurnIndex();
        int currentAP = currentNode.getCurrentAP();
        if (nextTurn)
        {
            turnIndex++;
            currentAP = trooper.getInitialActionPoints();
            if (isCommanderNearby(currentNode.getCell(), trooper, turnIndex, world))
            {
                currentAP += game.getCommanderAuraBonusActionPoints();
            }
        }

        LinkedList<Cell> nextCells = new LinkedList<Cell>();
        if (currentNode.getCell().getX() != world.getWidth() - 1)
        {
            nextCells.add(Cell.create(currentNode.getCell().getX() + 1, currentNode.getCell().getY()));
        }
        if (currentNode.getCell().getX() != 0)
        {
            nextCells.add(Cell.create(currentNode.getCell().getX() - 1, currentNode.getCell().getY()));
        }
        if (currentNode.getCell().getY() != world.getHeight() - 1)
        {
            nextCells.add(Cell.create(currentNode.getCell().getX(), currentNode.getCell().getY() + 1));
        }
        if (currentNode.getCell().getY() != 0)
        {
            nextCells.add(Cell.create(currentNode.getCell().getX(), currentNode.getCell().getY() - 1));
        }
        nextCells.add(currentNode.getCell());
        for (Cell cell : nextCells)
        {
            if (!isPassable(cell, world, trooper, turnIndex) && !cell.equals(start) && !cell.equals(end))
            {
                continue;
            }
            PathNode nextNode = new PathNode(cell, turnIndex, currentNode, currentAP - game.getStandingMoveCost(),
                    currentNode.getSpentAP() + game.getStandingMoveCost());
            allNodes.add(nextNode);
        }
        return calcDistanceRecurs2(trooper, start, end, allNodes.pollFirst(), allNodes, world, game);
    }

    private boolean isPassable(Cell cell, World world, Trooper self, int turnIndex)
    {
        if (CellType.FREE.equals(world.getCells()[cell.getX()][cell.getY()]))
        {
            return false;
        }
        for (Cell plannedTrooperPosition : RadioChannel.INSTANCE.getPlannedSquadPosition(self, turnIndex, world))
        {
            if (plannedTrooperPosition.equals(cell))
            {
                return false;
            }
        }
        return true;
    }
}

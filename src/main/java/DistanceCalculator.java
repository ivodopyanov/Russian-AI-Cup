/**
 * 
 */

import java.util.Comparator;
import java.util.LinkedList;
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

        private final World world;
        private final Game game;
        private final DistanceCalcContext context;

        public DistanceCellComparator(Trooper trooper, int startAP, Cell pivot, World world, Game game)
        {
            this.world = world;
            this.game = game;
            context = new DistanceCalcContext(trooper, startAP, world.getMoveIndex(), pivot);
        }

        @Override
        public int compare(Cell o1, Cell o2)
        {
            return INSTANCE.getDistance(context, o1, world, game) - INSTANCE.getDistance(context, o2, world, game);
        }
    }

    private static final class PathNodeComparator implements Comparator<PathNode>
    {
        private final Cell end;
        private final Game game;

        public PathNodeComparator(Cell end, Game game)
        {
            this.end = end;
            this.game = game;
        }

        @Override
        public int compare(PathNode o1, PathNode o2)
        {
            return calcF(o1) - calcF(o2);
        }

        //целевая функция А*
        private int calcF(PathNode pathNode)
        {
            //Вычисляем расстояние от начала как кол-во затраченных AP
            int distanceFromStart = pathNode.getSpentAP();
            //Вычисляем расстояние до конца как теоретическое кол-во шагов, через которые придется пройти * стоимость шага в AP
            int approxDistanceToCell = (Math.abs(pathNode.getCell().getX() - end.getX()) + Math.abs(pathNode.getCell()
                    .getY() - end.getY()))
                    * game.getStandingMoveCost();
            return distanceFromStart + approxDistanceToCell;
        }
    }

    public static final DistanceCalculator INSTANCE = new DistanceCalculator();

    //Кол-во очков действия, которое потратит боец trooper с исходным кол-вом очков действия startAP в ходе turnIndex на то, чтобы пройти из точки start в точку end 
    public int getDistance(DistanceCalcContext context, Cell end, World world, Game game)
    {
        TreeSet<PathNode> allNodes = new TreeSet<PathNode>(new PathNodeComparator(end, game));
        allNodes.add(new PathNode(context));
        PathNode endingNode = calcDistanceRecurs(end, allNodes, world, game);
        return endingNode.getSpentAP();
    }

    //Поиск кратчайшего пути для солдата trooper с начальным кол-вом очков действия AP в ходе turnIndex из точки start в точку end
    public LinkedList<PathNode> getPath(DistanceCalcContext context, Cell end, World world, Game game)
    {
        TreeSet<PathNode> allNodes = new TreeSet<PathNode>(new PathNodeComparator(end, game));
        allNodes.add(new PathNode(context));
        PathNode endingNode = calcDistanceRecurs(end, allNodes, world, game);
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
        if (TrooperType.COMMANDER.equals(trooper.getType()))
        {
            return false;
        }
        Trooper commander = Helper.INSTANCE.findTeammateTrooperByType(world, TrooperType.COMMANDER);
        if (commander == null)
        {
            return false;//Командира с нами больше нет!
        }

        boolean beginningOfTurn = RadioChannel.INSTANCE.getTurnOrder().indexOf(TrooperType.COMMANDER) > RadioChannel.INSTANCE
                .getTurnOrder().indexOf(trooper.getType());
        Cell commanderPosition = RadioChannel.INSTANCE.getPlannedTrooperPosition(commander.getId(), turnIndex,
                beginningOfTurn);
        if (commanderPosition == null)//Местоположение командира на этот ход еще не известно - такое может быть, если какой-то солдат запланирует подобрать бонус, а у командира приказов не будет
        {
            return false;
        }
        return this.isNeighbourCell(cell.getX(), cell.getY(), commanderPosition.getX(), commanderPosition.getY());
    }

    public boolean isNeighbourCell(int x1, int y1, int x2, int y2)
    {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public boolean isPassable(Cell cell, World world, Trooper self, int turnIndex)
    {
        if (!CellType.FREE.equals(world.getCells()[cell.getX()][cell.getY()]))
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

    private PathNode calcDistanceRecurs(Cell end, TreeSet<PathNode> allNodes, World world, Game game)
    {
        PathNode currentNode = allNodes.pollFirst();
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
            currentAP = currentNode.getTrooper().getInitialActionPoints();
            if (isCommanderNearby(currentNode.getCell(), currentNode.getTrooper(), turnIndex, world))
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
            if (!isPassable(cell, world, currentNode.getTrooper(), turnIndex) && !cell.equals(end))
            {
                continue;
            }
            PathNode nextNode = new PathNode(cell, turnIndex, currentNode, currentAP - game.getStandingMoveCost(),
                    currentNode.getSpentAP() + game.getStandingMoveCost(), currentNode.getTrooper());
            allNodes.add(nextNode);
        }
        return calcDistanceRecurs(end, allNodes, world, game);
    }
}

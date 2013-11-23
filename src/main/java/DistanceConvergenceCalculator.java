import java.util.*;
import java.util.Map.Entry;

import model.Game;
import model.Trooper;
import model.TrooperType;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 23 нояб. 2013 г.
 *
 */
public class DistanceConvergenceCalculator
{
    private static final class PathNodeConvergenceComparator implements Comparator<PathNode>
    {
        private final int currentTrooperTurnOrder;
        private final Map<Trooper, PathNode> currentBestPathNodes;

        public PathNodeConvergenceComparator(Trooper currentTrooper, Map<Trooper, PathNode> currentBestPathNodes)
        {
            currentTrooperTurnOrder = RadioChannel.INSTANCE.getTurnOrder().indexOf(currentTrooper);
            this.currentBestPathNodes = currentBestPathNodes;
        }

        @Override
        public int compare(PathNode o1, PathNode o2)
        {
            //Если один из PathNode происходит раньше (по ходам) другого - то обрабатываем его
            if (o1.getTurnIndex() != o2.getTurnIndex())
            {
                return o1.getTurnIndex() - o2.getTurnIndex();
            }
            //Если PathNode принадлежат разным солдатам, то обрабатываем того солдата, который будет ходить раньше
            if (o1.getTrooper().getId() != o2.getTrooper().getId())
            {
                return compareRelativeTurnOrder(o1.getTrooper().getType(), o2.getTrooper().getType());
            }
            double d1 = calcApproxDistanceToConvergencePoint(o1, currentBestPathNodes);
            double d2 = calcApproxDistanceToConvergencePoint(o2, currentBestPathNodes);
            if (d1 != d2)
            {
                return d1 > d2 ? -1 : 1;
            }
            return o1.getSpentAP() - o2.getSpentAP();
        }

        private int compareRelativeTurnOrder(TrooperType tt1, TrooperType tt2)
        {
            List<TrooperType> turnOrder = RadioChannel.INSTANCE.getTurnOrder();
            int index1 = turnOrder.indexOf(tt1);
            if (index1 < currentTrooperTurnOrder)
            {
                index1 += turnOrder.size();
            }
            int index2 = turnOrder.indexOf(tt2);
            if (index2 < currentTrooperTurnOrder)
            {
                index2 += turnOrder.size();
            }
            return index1 - index2;
        }

    }

    private static final double PERIMETER_CONVERGENCE_MULTIPLIER = 2.2;//Коэффициент, определяющий минимальную длину периметра, при которой считается, что солдаты сошлись вместе;

    public static final DistanceConvergenceCalculator INSTANCE = new DistanceConvergenceCalculator();

    //Рассчитываем целевую функцию, описывающую расстояние до точки схожддения солдат
    //Как длина периметра, образованного лучшими текущими PathNode каждого солдата
    //Плюс кол-во затраченных AP на текущий PathNode
    private static double calcApproxDistanceToConvergencePoint(PathNode pathNode,
            Map<Trooper, PathNode> currentBestPathNodes)
    {
        double perimeter = 0;
        Cell start = pathNode.getCell();
        Cell end = null;
        for (Entry<Trooper, PathNode> currentBestPathNode : currentBestPathNodes.entrySet())
        {
            if (currentBestPathNode.getKey().getId() == pathNode.getTrooper().getId())
            {
                continue;
            }
            end = currentBestPathNode.getValue().getCell();
            perimeter += Helper.INSTANCE.distance(start, end);
            start = end;
        }
        end = pathNode.getCell();
        perimeter += Helper.INSTANCE.distance(start, end);
        return (int)perimeter;
    }

    //Поиск кратчайшего пути для всех солдат, который приведет их всех в одно место. 
    public Map<Trooper, List<PathNode>> getConvergencePaths(Trooper currentTrooper, List<DistanceCalcContext> contexts,
            World world, Game game)
    {
        Map<Trooper, PathNode> currentBestPathNodes = new HashMap<Trooper, PathNode>();
        for (DistanceCalcContext context : contexts)
        {
            PathNode startPathNode = new PathNode(context);
            currentBestPathNodes.put(context.getTrooper(), startPathNode);
        }
        TreeSet<PathNode> allNodes = new TreeSet<PathNode>(new PathNodeConvergenceComparator(currentTrooper,
                currentBestPathNodes));
        allNodes.addAll(currentBestPathNodes.values());
        Map<Trooper, PathNode> paths = calcDistanceRecurs(currentTrooper, contexts, allNodes, currentBestPathNodes,
                world, game);
        if (paths == null)
        {
            return null;
        }
        Map<Trooper, List<PathNode>> result = new HashMap<Trooper, List<PathNode>>();
        for (Trooper type : paths.keySet())
        {
            LinkedList<PathNode> trooperPath = new LinkedList<PathNode>();
            PathNode endingNode = paths.get(type);
            while (endingNode != null)
            {
                trooperPath.push(endingNode);
                endingNode = endingNode.getPrevPathNode();
            }
            result.put(type, trooperPath);
        }
        return result;
    }

    private Map<Trooper, PathNode> calcDistanceRecurs(Trooper currentTrooper, List<DistanceCalcContext> contexts,
            TreeSet<PathNode> allNodes, Map<Trooper, PathNode> currentBestPathNodes, World world, Game game)
    {
        PathNode currentNode = allNodes.pollFirst();
        if (currentNode == null)
        {
            return null;//Путь недостижим в принципе
        }
        currentBestPathNodes.put(currentNode.getTrooper(), currentNode);
        if (calcApproxDistanceToConvergencePoint(currentNode, currentBestPathNodes) < PERIMETER_CONVERGENCE_MULTIPLIER
                * contexts.size())
        {
            return currentBestPathNodes;
        }
        boolean nextTurn = currentNode.getCurrentAP() < game.getStandingMoveCost();
        int turnIndex = currentNode.getTurnIndex();
        int currentAP = currentNode.getCurrentAP();
        if (nextTurn)
        {
            turnIndex++;
            currentAP = currentNode.getTrooper().getInitialActionPoints();
            if (DistanceCalculator.INSTANCE.isCommanderNearby(currentNode.getCell(), currentNode.getTrooper(),
                    turnIndex, world))
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
            if (!DistanceCalculator.INSTANCE.isPassable(cell, world, currentNode.getTrooper(), turnIndex))
            {
                continue;
            }
            PathNode nextNode = new PathNode(cell, turnIndex, currentNode, currentAP - game.getStandingMoveCost(),
                    currentNode.getSpentAP() + game.getStandingMoveCost(), currentNode.getTrooper());
            allNodes.add(nextNode);
        }
        return calcDistanceRecurs(currentTrooper, contexts, allNodes, currentBestPathNodes, world, game);
    }
}

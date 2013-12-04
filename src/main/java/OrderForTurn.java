import java.util.LinkedList;

import model.Move;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 22 нояб. 2013 г.
 *
 */
public class OrderForTurn
{
    private final LinkedList<Move> orders;
    private final LinkedList<PathNode> pathNodes;
    private final int turnIndex;

    public OrderForTurn(LinkedList<Move> orders, int turnIndex, LinkedList<PathNode> pathNodes)
    {
        this.orders = orders;
        this.turnIndex = turnIndex;
        this.pathNodes = pathNodes;
    }

    public void add(OrderForTurn orderForTurn)
    {
        orderForTurn.getPathNodes().pop();
        pathNodes.addAll(orderForTurn.getPathNodes());
        orderForTurn.getOrders().pop();
        orders.addAll(orderForTurn.getOrders());
    }

    public Cell getEnd()
    {
        if (orders.isEmpty())
        {
            return null;
        }
        return Cell.create(orders.peekLast().getX(), orders.peekLast().getY());
    }

    public LinkedList<Move> getOrders()
    {
        return orders;
    }

    public LinkedList<PathNode> getPathNodes()
    {
        return pathNodes;
    }

    public Cell getStart()
    {
        if (orders.isEmpty())
        {
            return null;
        }
        return Cell.create(orders.peekFirst().getX(), orders.peekFirst().getY());
    }

    public int getTurnIndex()
    {
        return turnIndex;
    }
}
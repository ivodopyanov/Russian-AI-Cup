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
    private final int turnIndex;

    public OrderForTurn(LinkedList<Move> orders, int turnIndex)
    {
        this.orders = orders;
        this.turnIndex = turnIndex;
    }

    public Cell getEnd()
    {
        return Cell.create(orders.peekLast().getX(), orders.peekLast().getY());
    }

    public LinkedList<Move> getOrders()
    {
        return orders;
    }

    public Cell getStart()
    {
        return Cell.create(orders.peekFirst().getX(), orders.peekFirst().getY());
    }

    public int getTurnIndex()
    {
        return turnIndex;
    }
}
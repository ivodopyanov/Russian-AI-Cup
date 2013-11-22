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
    private final Cell start;
    private final Cell end;
    private final LinkedList<Move> orders;
    private final int turnIndex;

    public OrderForTurn(Cell start, Cell end, LinkedList<Move> orders, int turnIndex)
    {
        this.start = start;
        this.end = end;
        this.orders = orders;
        this.turnIndex = turnIndex;
    }

    public Cell getEnd()
    {
        return end;
    }

    public LinkedList<Move> getOrders()
    {
        return orders;
    }

    public Cell getStart()
    {
        return start;
    }

    public int getTurnIndex()
    {
        return turnIndex;
    }
}
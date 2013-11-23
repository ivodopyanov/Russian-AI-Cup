import java.util.Comparator;
import java.util.List;

import model.Trooper;
import model.TrooperType;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public abstract class OrderBuilderImpl implements OrderBuilder
{
    protected static class OrderEdictionSorter implements Comparator<Trooper>
    {
        private final int selfTurnOrderIndex;

        public OrderEdictionSorter(TrooperType currentTrooperType)
        {
            List<TrooperType> turnOrder = RadioChannel.INSTANCE.getTurnOrder();
            selfTurnOrderIndex = turnOrder.indexOf(currentTrooperType);
        }

        @Override
        public int compare(Trooper o1, Trooper o2)
        {
            if (TrooperType.COMMANDER.equals(o1.getType()))
            {
                return -1;
            }
            if (TrooperType.COMMANDER.equals(o2.getType()))
            {
                return 1;
            }
            int index1 = updateIndex(RadioChannel.INSTANCE.getTurnOrder().indexOf(o1.getType()));
            int index2 = updateIndex(RadioChannel.INSTANCE.getTurnOrder().indexOf(o2.getType()));
            return index1 - index2;
        }

        private int updateIndex(int index)
        {
            int result = index - selfTurnOrderIndex;
            if (result < 0)
            {
                result += RadioChannel.INSTANCE.getTurnOrder().size();
            }
            return result;
        }

    }

    private final OrderType type;

    protected OrderBuilderImpl(OrderType type)
    {
        this.type = type;
    }

    @Override
    public OrderType getType()
    {
        return type;
    }

}
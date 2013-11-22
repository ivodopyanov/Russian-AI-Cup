/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public enum OrderType
{
    PATROL(10), PICKUP_BONUSES(20), ATTACK(30);

    private final int importance;

    private OrderType(int importance)
    {
        this.importance = importance;
    }

    public int getImportance()
    {
        return importance;
    }
}
import java.util.List;

import model.Bonus;
import model.Game;
import model.Trooper;
import model.World;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class OrderBuilderAttack extends OrderBuilderImpl
{
    public OrderBuilderAttack()
    {
        super(OrderType.ATTACK);
    }

    @Override
    public void buildOrder(List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies, World world,
            Game game)
    {
    }

    @Override
    public boolean isApplicable(List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game)
    {
        return !visibleEnemies.isEmpty();
    }
}
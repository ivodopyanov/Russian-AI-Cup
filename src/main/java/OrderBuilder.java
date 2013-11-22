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
public interface OrderBuilder
{
    void buildOrder(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game);

    OrderType getType();

    boolean isApplicable(Trooper self, List<Trooper> squad, List<Bonus> visibleBonuses, List<Trooper> visibleEnemies,
            World world, Game game);
}

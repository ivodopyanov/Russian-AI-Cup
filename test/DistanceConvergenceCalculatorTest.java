import model.Trooper;
import model.TrooperStance;
import model.TrooperType;

import org.junit.Test;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 25 нояб. 2013 г.
 *
 */
public class DistanceConvergenceCalculatorTest
{

    @Test
    public void test()
    {
        Trooper medic = new Trooper(5, 28, 19, 2, 1, true, TrooperType.FIELD_MEDIC, TrooperStance.STANDING, 100, 100,
                12, 10, 7.0, 5.0, 2, 9, 12, 15, 9, false, true, false);
        Trooper commander = new Trooper(4, 27, 17, 2, 0, true, TrooperType.COMMANDER, TrooperStance.STANDING, 100, 100,
                10, 10, 8.0, 7.0, 3, 15, 20, 25, 15, false, false, false);
        Trooper soldier = new Trooper(6, 29, 18, 2, 2, true, TrooperType.SOLDIER, TrooperStance.STANDING, 120, 100, 12,
                10, 7.0, 8.0, 4, 25, 30, 35, 25, true, false, false);
        DistanceCalcContext medicContext = new DistanceCalcContext(medic, 4, 1, Cell.create(24, 19));
        DistanceCalcContext commanderContext = new DistanceCalcContext(commander, 0, 1, Cell.create(28, 15));
        DistanceCalcContext soldierContext = new DistanceCalcContext(soldier, 10, 1, Cell.create(29, 18));
    }
}

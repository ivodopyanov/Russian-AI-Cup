import java.util.Arrays;

import model.*;

import org.junit.Test;

public class DistanceCalculatorTest
{
    @Test
    public void testPosition1()
    {
        CellType[][] cells = TestHelper.convertMap(TestHelper.MAP1);
        Player[] players = TestHelper.getPlayers();
        //@formatter:off
        Trooper[] troopers=new Trooper[]{
                TestHelper.createMedicHuman(1, 6, 2, 1, false, true, true),
                TestHelper.createCommanderHuman(2,6,3,2,false,true, true),
                TestHelper.createSoldierHuman(3, 5, 3, 3, true, true, true)};
        //@formatter:on
        Bonus[] bonuses = new Bonus[] {};
        boolean[] cellVisibility = new boolean[30 * 30 * 20 * 20 * 3 * 2];
        Game game = TestHelper.createGame();

        Move move = new Move();

        World world = new World(0, 30, 20, players, troopers, bonuses, cells, new boolean[0]);
        StrategyTrooper commanderStrategy = new StrategyTrooperImpl(Arrays.asList(Strategies.DEFAULT_MOVES));
        commanderStrategy.move(troopers[1], world, game, move);
    }
}

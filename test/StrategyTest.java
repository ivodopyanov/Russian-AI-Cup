import model.*;

import org.junit.Test;

public class StrategyTest
{
    @Test
    public void testPosition1()
    {
        CellType[][] cells = TestHelper.convertMap(TestHelper.MAP2);
        Player[] players = TestHelper.getPlayers();
        //@formatter:off
        Trooper[] originalPosition=new Trooper[]{
                TestHelper.createMedicHuman(1, 0, 1, 1, false, true, false),
                TestHelper.createCommanderHuman(2, 2, 2, 2,false,false, false),
                TestHelper.createSoldierHuman(3, 0, 1, 3, true, false, false)};
        
        Trooper[] testPosition=new Trooper[]{
                TestHelper.createMedicHuman(1, 5, 7, 1, true, true, true),
                TestHelper.createCommanderHuman(2, 5, 6, 2,true,true, true),
                TestHelper.createSoldierHuman(3, 5, 5, 3, true, false, false)};
        //@formatter:on
        Bonus[] bonuses = new Bonus[] {};
        boolean[] cellVisibility = new boolean[30 * 30 * 20 * 20 * 3 * 2];
        Game game = TestHelper.createGame();

        Move move = new Move();

        World originalWorld = new World(0, 30, 20, players, originalPosition, bonuses, cells, cellVisibility);
        World testWorld = new World(0, 30, 20, players, testPosition, bonuses, cells, cellVisibility);
        Strategy strategy = new MyStrategy();
        strategy.move(originalPosition[0], originalWorld, game, move);

        strategy.move(testPosition[0], testWorld, game, move);
        strategy.move(testPosition[1], testWorld, game, move);
        strategy.move(testPosition[2], testWorld, game, move);
    }
}

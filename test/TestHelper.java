import model.*;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 19 нояб. 2013 г.
 *
 */
public class TestHelper
{
    public static final int HUMAN_ID = 1;
    public static final int COMPUTER_1_ID = 1;
    public static final int COMPUTER_2_ID = 1;
    public static final int COMPUTER_3_ID = 1;

    //@formatter:off
    public static final String MAP1=
                                        "000020000000000"+
                                        "000000000000000"+
                                        "000020000000000"+
                                        "000330001110000"+
                                        "202330001221100"+
                                        "000000000112210"+
                                        "000000000001210"+
                                        "000220000000110"+
                                        "202332000000000"+
                                        "323332000000000";
    //@formatter:on

    public static CellType[][] convertMap(String map)
    {
        CellType[][] cells = new CellType[30][20];
        for (int y = 0; y < 10; y++)
        {
            for (int x = 0; x < 15; x++)
            {
                char ch = map.charAt(y * 30 + x);
                CellType cellType;
                switch (ch)
                {
                case '1':
                    cellType = CellType.LOW_COVER;
                case '2':
                    cellType = CellType.MEDIUM_COVER;
                case '3':
                    cellType = CellType.HIGH_COVER;
                default:
                    cellType = CellType.FREE;
                }
                cells[x][y] = cellType;
                cells[29 - x][y] = cellType;
                cells[x][19 - y] = cellType;
                cells[29 - x][19 - y] = cellType;
            }
        }
        return cells;
    }

    public static Trooper createCommanderHuman(int id, int x, int y, int teammate_index, boolean holdingGrenade,
            boolean holdingMedikit, boolean holdingFieldRation)
    {
        return new Trooper(id, x, y, HUMAN_ID, teammate_index, true, TrooperType.COMMANDER, TrooperStance.STANDING,
                100, 100, 12, 12, 8, 7, 3, 15, 20, 25, 15, holdingGrenade, holdingMedikit, holdingFieldRation);
    }

    public static Game createGame()
    {
        return null;
    }

    public static Trooper createMedicHuman(int id, int x, int y, int teammate_index, boolean holdingGrenade,
            boolean holdingMedikit, boolean holdingFieldRation)
    {
        return new Trooper(id, x, y, HUMAN_ID, teammate_index, true, TrooperType.FIELD_MEDIC, TrooperStance.STANDING,
                100, 100, 12, 12, 7, 5, 2, 9, 12, 15, 9, holdingGrenade, holdingMedikit, holdingFieldRation);
    }

    public static Trooper createSoldierHuman(int id, int x, int y, int teammate_index, boolean holdingGrenade,
            boolean holdingMedikit, boolean holdingFieldRation)
    {
        return new Trooper(id, x, y, HUMAN_ID, teammate_index, true, TrooperType.SOLDIER, TrooperStance.STANDING, 120,
                100, 12, 12, 7, 8, 4, 25, 30, 35, 25, holdingGrenade, holdingMedikit, holdingFieldRation);
    }

    public static Player[] getPlayers()
    {
        Player human = new Player(1, "Human", 0, false, -1, -1);
        Player comp1 = new Player(2, "Comp1", 0, false, -1, -1);
        Player comp2 = new Player(3, "Comp2", 0, false, -1, -1);
        Player comp3 = new Player(4, "Comp3", 0, false, -1, -1);
        return new Player[] { human, comp1, comp2, comp3 };
    }

}

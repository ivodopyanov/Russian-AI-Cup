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
    private static final int MOVE_COUNT = 50;
    private static final int LAST_PLAYER_ELIMINATION_SCORE = 100;
    private static final int PLAYER_ELIMINATION_SCORE = 50;
    private static final int TROOPER_ELIMINATION_SCORE = 25;
    private static final double TROOPER_DAMAGE_SCORE_FACTOR = 1;
    private static final int STANCE_CHANGE_COST = 2;
    private static final int STANDING_MOVE_COST = 2;
    private static final int KNEELING_MOVE_COST = 4;
    private static final int PRONE_MOVE_COST = 6;
    private static final int COMMANDER_AURA_BONUS_ACTION_POINTS = 2;
    private static final double COMMANDER_AURA_RANGE = 5;
    private static final int COMMANDER_REQUEST_ENEMY_DISPOSITION_COST = 10;
    private static final int COMMANDER_REQUEST_ENEMY_DISPOSITION_MAX_OFFSET = 5;
    private static final int FIELD_MEDIC_HEAL_COST = 1;
    private static final int FIELD_MEDIC_HEAL_BONUS_HITPOINTS = 5;
    private static final int FIELD_MEDIC_HEAL_SELF_BONUS_HITPOINTS = 3;
    private static final double SNIPER_STANDING_STEALTH_BONUS = 0;
    private static final double SNIPER_KNEELING_STEALTH_BONUS = 0.5;
    private static final double SNIPER_PRONE_STEALTH_BONUS = 1;
    private static final double SNIPER_STANDING_SHOOTING_RANGE_BONUS = 0;
    private static final double SNIPER_KNEELING_SHOOTING_RANGE_BONUS = 1;
    private static final double SNIPER_PRONE_SHOOTING_RANGE_BONUS = 2;
    private static final double SCOUT_STEALTH_BONUS_NEGATION = 0;
    private static final int GRENADE_THROW_COST = 8;
    private static final double GRENADE_THROW_RANGE = 5;
    private static final int GRENADE_DIRECT_DAMAGE = 80;
    private static final int GRENADE_COLLATERAL_DAMAGE = 60;
    private static final int MEDIKIT_USE_COST = 2;
    private static final int MEDIKIT_BONUS_HITPOINTS = 50;
    private static final int MEDIKIT_HEAL_SELF_BONUS_HITPOINTS = 30;
    private static final int FIELD_RATION_EAT_COST = 2;
    private static final int FIELD_RATION_BONUS_ACTION_POINTS = 5;

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
    
    public static final String MAP2=
                                        "000000000000000"+
                                        "000000000000000"+
                                        "000000033330033"+
                                        "000000033330033"+
                                        "000000033330033"+
                                        "033330033330033"+
                                        "033330000000000"+
                                        "033330000000000"+
                                        "033330033330001"+
                                        "000000033330012";
    //@formatter:on

    public static CellType[][] convertMap(String map)
    {
        CellType[][] cells = new CellType[30][20];
        for (int y = 0; y < 10; y++)
        {
            for (int x = 0; x < 15; x++)
            {
                char ch = map.charAt(y * 15 + x);
                CellType cellType;
                switch (ch)
                {
                case '1':
                    cellType = CellType.LOW_COVER;
                    break;
                case '2':
                    cellType = CellType.MEDIUM_COVER;
                    break;
                case '3':
                    cellType = CellType.HIGH_COVER;
                    break;
                default:
                    cellType = CellType.FREE;
                    break;
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
        return new Game(MOVE_COUNT, LAST_PLAYER_ELIMINATION_SCORE, PLAYER_ELIMINATION_SCORE, TROOPER_ELIMINATION_SCORE,
                TROOPER_DAMAGE_SCORE_FACTOR, STANCE_CHANGE_COST, STANDING_MOVE_COST, KNEELING_MOVE_COST,
                PRONE_MOVE_COST, COMMANDER_AURA_BONUS_ACTION_POINTS, COMMANDER_AURA_RANGE,
                COMMANDER_REQUEST_ENEMY_DISPOSITION_COST, COMMANDER_REQUEST_ENEMY_DISPOSITION_MAX_OFFSET,
                FIELD_MEDIC_HEAL_COST, FIELD_MEDIC_HEAL_BONUS_HITPOINTS, FIELD_MEDIC_HEAL_SELF_BONUS_HITPOINTS,
                SNIPER_STANDING_STEALTH_BONUS, SNIPER_KNEELING_STEALTH_BONUS, SNIPER_PRONE_STEALTH_BONUS,
                SNIPER_STANDING_SHOOTING_RANGE_BONUS, SNIPER_KNEELING_SHOOTING_RANGE_BONUS,
                SNIPER_PRONE_SHOOTING_RANGE_BONUS, SCOUT_STEALTH_BONUS_NEGATION, GRENADE_THROW_COST,
                GRENADE_THROW_RANGE, GRENADE_DIRECT_DAMAGE, GRENADE_COLLATERAL_DAMAGE, MEDIKIT_USE_COST,
                MEDIKIT_BONUS_HITPOINTS, MEDIKIT_HEAL_SELF_BONUS_HITPOINTS, FIELD_RATION_EAT_COST,
                FIELD_RATION_BONUS_ACTION_POINTS);
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

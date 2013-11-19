/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 *
 */
public class Strategies
{
    public static final MoveEval GO_TO_BONUS = new MoveEvalGoToBonus();
    public static final MoveEval GO_TO_WOUNDED_TEAMMATE = new MoveEvalGoToWoundedTeammate();
    public static final MoveEval HEAL_TEAMMATE = new MoveEvalHealTeammate();
    public static final MoveEval KEEP_FORMATION = new MoveEvalKeepFormationOnLeader();
    public static final MoveEval LEADER_MOVE = new MoveEvalLeaderMove();
    public static final MoveEval SHOOT_ENEMY = new MoveEvalShootEnemy();
    public static final MoveEval USE_GRENADE = new MoveEvalUseGrenade();
    public static final MoveEval USE_MEDIKIT = new MoveEvalUseMedikit();

    public static final MoveEval[] DEFAULT_MOVES = new MoveEval[] { GO_TO_BONUS, GO_TO_WOUNDED_TEAMMATE,
            KEEP_FORMATION, LEADER_MOVE, SHOOT_ENEMY, USE_GRENADE, USE_MEDIKIT };
    public static final MoveEval[] MEDIC_MOVES = new MoveEval[] { GO_TO_BONUS, KEEP_FORMATION, LEADER_MOVE,
            SHOOT_ENEMY, USE_GRENADE, USE_MEDIKIT, HEAL_TEAMMATE };
}

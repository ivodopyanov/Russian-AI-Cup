/**
 * 
 */
package trooperstrategy;

import moveevaluator.*;

/**
 * @author ivodopyanov
 * @since 16 нояб. 2013 г.
 *
 */
public class Strategies
{
    public static final MoveEvaluator GO_TO_BONUS = new GoToBonus();
    public static final MoveEvaluator GO_TO_WOUNDED_TEAMMATE = new GoToWoundedTeammate();
    public static final MoveEvaluator HEAL_TEAMMATE = new HealTeammate();
    public static final MoveEvaluator KEEP_FORMATION = new KeepFormationOnLeader();
    public static final MoveEvaluator LEADER_MOVE = new LeaderMove();
    public static final MoveEvaluator SHOOT_ENEMY = new ShootEnemy();
    public static final MoveEvaluator USE_GRENADE = new UseGrenade();
    public static final MoveEvaluator USE_MEDIKIT = new UseMedikit();

    public static final MoveEvaluator[] DEFAULT_MOVES = new MoveEvaluator[] { GO_TO_BONUS, GO_TO_WOUNDED_TEAMMATE,
            KEEP_FORMATION, LEADER_MOVE, SHOOT_ENEMY, USE_GRENADE, USE_MEDIKIT };
    public static final MoveEvaluator[] MEDIC_MOVES = new MoveEvaluator[] { GO_TO_BONUS, KEEP_FORMATION, LEADER_MOVE,
            SHOOT_ENEMY, USE_GRENADE, USE_MEDIKIT, HEAL_TEAMMATE };
}

import java.util.Comparator;

import model.ActionType;
import model.Direction;

public class MoveEvaluation
{
    public static final Comparator<MoveEvaluation> MOVE_EVALUATION_COMPARATOR = new Comparator<MoveEvaluation>()
    {
        @Override
        public int compare(MoveEvaluation arg0, MoveEvaluation arg1)
        {
            return (int)(arg1.getEvaluation() - arg0.getEvaluation());
        }
    };

    public static MoveEvaluation heal(int x, int y)
    {
        return new MoveEvaluation(ActionType.HEAL, null, x, y);
    }

    public static MoveEvaluation move(Direction direction)
    {
        return new MoveEvaluation(ActionType.MOVE, direction, 0, 0);
    }

    public static MoveEvaluation move(int x, int y)
    {
        return new MoveEvaluation(ActionType.MOVE, null, x, y);
    }

    public static MoveEvaluation shoot(int x, int y)
    {
        return new MoveEvaluation(ActionType.SHOOT, null, x, y);
    }

    public static MoveEvaluation throwGrenade(int x, int y)
    {
        return new MoveEvaluation(ActionType.THROW_GRENADE, null, x, y);
    }

    public static MoveEvaluation useFieldRation()
    {
        return new MoveEvaluation(ActionType.EAT_FIELD_RATION, null, 0, 0);
    }

    public static MoveEvaluation useMedikit(int x, int y)
    {
        return new MoveEvaluation(ActionType.USE_MEDIKIT, null, x, y);
    }

    private final ActionType action;
    private final Direction direction;
    private final int x;
    private final int y;
    private double evaluation;

    public MoveEvaluation(ActionType action, Direction direction, int x, int y)
    {
        this.action = action;
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MoveEvaluation))
        {
            return false;
        }
        MoveEvaluation md = (MoveEvaluation)o;
        return getAction() == md.getAction() && getDirection() == md.getDirection() && getX() == md.getX()
                && getY() == md.getY();
    }

    public ActionType getAction()
    {
        return action;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public double getEvaluation()
    {
        return evaluation;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public int hashCode()
    {
        return action.hashCode() + direction.hashCode() + x + y;
    }

    public void setEvaluation(double evaluation)
    {
        this.evaluation = evaluation;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder().append("Action = ").append(action);
        if (direction != null)
        {
            sb.append(", direction = ").append(direction);
        }
        else
        {
            sb.append(", direction is null");
        }
        sb.append(", x = ").append(x).append(", y = ").append(y).append(", eval = ").append(evaluation);
        return sb.toString();
    }

}

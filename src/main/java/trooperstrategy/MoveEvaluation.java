package trooperstrategy;

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
			return (int) (arg0.getEvaluation() - arg1.getEvaluation());
		}
	};
	
	public static MoveEvaluation move(Direction direction)
	{
		return new MoveEvaluation(ActionType.MOVE, direction, 0, 0);
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
		MoveEvaluation md = (MoveEvaluation) o;
		return getAction() == md.getAction()
		        && getDirection() == md.getDirection() && getX() == md.getX()
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
	
}

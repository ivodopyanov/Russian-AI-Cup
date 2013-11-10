package trooperstrategy;

import java.util.Arrays;

import moveevaluator.GoToBonusMoveEvaluatorImpl;
import moveevaluator.MoveEvaluator;

public class CommanderStrategyImpl extends TrooperStrategyImpl
{
	
	public CommanderStrategyImpl()
	{
		super(Arrays.<MoveEvaluator> asList(new GoToBonusMoveEvaluatorImpl()));
	}
	
}

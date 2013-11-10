package trooperstrategy;

import java.util.Arrays;

import moveevaluator.GoToBonusMoveEvaluatorImpl;
import moveevaluator.MoveEvaluator;

public class SniperStrategyImpl extends TrooperStrategyImpl
{
	
	public SniperStrategyImpl()
	{
		super(Arrays.<MoveEvaluator> asList(new GoToBonusMoveEvaluatorImpl()));
	}
}

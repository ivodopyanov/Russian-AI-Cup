package trooperstrategy;

import java.util.Arrays;

import moveevaluator.GoToBonusMoveEvaluatorImpl;
import moveevaluator.MoveEvaluator;

public class SoldierStrategyImpl extends TrooperStrategyImpl
{
	
	public SoldierStrategyImpl()
	{
		super(Arrays.<MoveEvaluator> asList(new GoToBonusMoveEvaluatorImpl()));
	}
}

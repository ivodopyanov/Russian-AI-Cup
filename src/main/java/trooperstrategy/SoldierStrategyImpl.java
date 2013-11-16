package trooperstrategy;

import java.util.Arrays;

import moveevaluator.MoveEvaluator;

public class SoldierStrategyImpl extends TrooperStrategyImpl
{

    public SoldierStrategyImpl()
    {
        super(Arrays.<MoveEvaluator> asList(Strategies.DEFAULT_MOVES));
    }
}

package trooperstrategy;

import java.util.Arrays;

import moveevaluator.MoveEvaluator;

public class SniperStrategyImpl extends TrooperStrategyImpl
{

    public SniperStrategyImpl()
    {
        super(Arrays.<MoveEvaluator> asList(Strategies.DEFAULT_MOVES));
    }
}

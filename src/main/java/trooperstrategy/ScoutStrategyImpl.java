package trooperstrategy;

import java.util.Arrays;

import moveevaluator.MoveEvaluator;

public class ScoutStrategyImpl extends TrooperStrategyImpl
{

    public ScoutStrategyImpl()
    {
        super(Arrays.<MoveEvaluator> asList(Strategies.DEFAULT_MOVES));
    }
}

package trooperstrategy;

import java.util.Arrays;

import moveevaluator.MoveEvaluator;

public class CommanderStrategyImpl extends TrooperStrategyImpl
{
    public CommanderStrategyImpl()
    {
        super(Arrays.<MoveEvaluator> asList(Strategies.DEFAULT_MOVES));
    }
}

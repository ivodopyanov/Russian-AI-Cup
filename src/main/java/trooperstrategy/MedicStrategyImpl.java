package trooperstrategy;

import java.util.Arrays;

import moveevaluator.MoveEvaluator;

public class MedicStrategyImpl extends TrooperStrategyImpl
{

    public MedicStrategyImpl()
    {
        super(Arrays.<MoveEvaluator> asList(Strategies.MEDIC_MOVES));
    }

}

import helpers.DistanceCalculator;
import helpers.Helper;
import helpers.RadioChannel;

import java.util.HashMap;
import java.util.Map;

import model.*;
import trooperstrategy.*;

public final class MyStrategy implements Strategy
{

    private static final Map<TrooperType, TrooperStrategy> TROOPER_STRATEGIES = new HashMap<TrooperType, TrooperStrategy>();
    static
    {
        TROOPER_STRATEGIES.put(TrooperType.COMMANDER, new CommanderStrategyImpl());
        TROOPER_STRATEGIES.put(TrooperType.FIELD_MEDIC, new MedicStrategyImpl());
        TROOPER_STRATEGIES.put(TrooperType.SCOUT, new ScoutStrategyImpl());
        TROOPER_STRATEGIES.put(TrooperType.SNIPER, new SniperStrategyImpl());
        TROOPER_STRATEGIES.put(TrooperType.SOLDIER, new SoldierStrategyImpl());
    }

    @Override
    public void move(Trooper self, World world, Game game, Move move)
    {
        if (!DistanceCalculator.INSTANCE.isInitialized())
        {
            DistanceCalculator.INSTANCE.init(world);
            Helper.INSTANCE.init(self);
            RadioChannel.INSTANCE.init(world);
        }

        else if (Helper.INSTANCE.getFirstTrooperToMove().equals(self.getType()))
        {
            RadioChannel.INSTANCE.turnPassed();
        }
        TROOPER_STRATEGIES.get(self.getType()).move(self, world, game, move);
    }
}

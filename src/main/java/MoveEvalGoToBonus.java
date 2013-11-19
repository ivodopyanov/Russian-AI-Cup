import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.*;

public class MoveEvalGoToBonus extends MoveEvalImpl
{

    private static class BonusComparatorByImportance implements Comparator<Bonus>
    {

        private final Trooper self;
        private final World world;
        private final Game game;

        public BonusComparatorByImportance(Trooper self, World world, Game game)
        {
            this.self = self;
            this.world = world;
            this.game = game;
        }

        @Override
        public int compare(Bonus o1, Bonus o2)
        {
            if (BonusType.FIELD_RATION.equals(o1.getType()) && BonusType.MEDIKIT.equals(o2.getType()))
            {
                return 1;
            }
            if (BonusType.FIELD_RATION.equals(o1.getType()) && BonusType.GRENADE.equals(o2.getType()))
            {
                return 1;
            }
            if (BonusType.GRENADE.equals(o1.getType()) && BonusType.MEDIKIT.equals(o2.getType()))
            {
                return Helper.INSTANCE.findWoundedTeammates(world).isEmpty() ? -1 : 1;
            }
            return DistanceCalculator.INSTANCE
                    .getDistance(self.getX(), self.getY(), o1.getX(), o1.getY(), world, false)
                    - DistanceCalculator.INSTANCE.getDistance(self.getX(), self.getY(), o2.getX(), o2.getY(), world,
                            false);
        }

    }

    public static final MoveEvalGoToBonus INSTANCE = new MoveEvalGoToBonus();

    @Override
    public void evaluate(Trooper self, World world, Game game)
    {
        if (Helper.INSTANCE.getMoveCost(self, game) > self.getActionPoints())
        {
            return;
        }
        List<Bonus> pickableBonuses = new LinkedList<Bonus>();
        for (Bonus bonus : world.getBonuses())
        {
            if ((BonusType.FIELD_RATION.equals(bonus.getType()) && !self.isHoldingFieldRation())
                    || (BonusType.GRENADE.equals(bonus.getType()) && !self.isHoldingGrenade())
                    || (BonusType.MEDIKIT.equals(bonus.getType()) && !self.isHoldingMedikit()))
            {
                pickableBonuses.add(bonus);
            }

        }
        if (pickableBonuses.isEmpty())
        {
            return;
        }
        Collections.sort(pickableBonuses, new BonusComparatorByImportance(self, world, game));

        for (int i = 0; i < pickableBonuses.size(); i++)
        {
            Bonus bonus = pickableBonuses.get(i);
            int distance = DistanceCalculator.INSTANCE.getDistance(self.getX(), self.getY(), bonus.getX(),
                    bonus.getY(), world, false);
            MoveEvaluation moveEvaluation = MoveEvaluation.move(bonus.getX(), bonus.getY());
            MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation,
                    WeightFunctions.bonusWeightFunction(bonus.getType(), distance));
        }
    }
}

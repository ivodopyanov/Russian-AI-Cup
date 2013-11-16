package moveevaluator;

import helpers.DistanceCalculator;
import helpers.Helper;
import helpers.WeightFunctions;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.*;
import trooperstrategy.MoveEvaluation;

public class GoToBonus extends MoveEvaluatorImpl
{
    private static class BonusComparatorByImportance implements Comparator<Bonus>
    {

        private Trooper self;
        private World world;
        private Game game;

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
            return DistanceCalculator.INSTANCE.getDistance(self.getX(), self.getY(), o1.getX(), o1.getY())
                    - DistanceCalculator.INSTANCE.getDistance(self.getX(), self.getY(), o2.getX(), o2.getY());
        }

        public void init(Trooper self, World world, Game game)
        {
            this.self = self;
            this.world = world;
            this.game = game;
        }

    }

    public static final GoToBonus INSTANCE = new GoToBonus();

    private static BonusComparatorByImportance BONUS_COMPARATOR = new BonusComparatorByImportance();

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
        BONUS_COMPARATOR.init(self, world, game);
        Collections.sort(pickableBonuses, BONUS_COMPARATOR);

        for (int i = 0; i < pickableBonuses.size(); i++)
        {
            Bonus bestBonus = pickableBonuses.get(i);
            for (Direction direction : Helper.INSTANCE.getDirectionForPath(self.getX(), self.getY(), bestBonus.getX(),
                    bestBonus.getY()))
            {
                MoveEvaluation moveEvaluation = MoveEvaluation.move(direction);
                MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation, WeightFunctions.bonusWeightFunction(i));
            }
        }
    }
}
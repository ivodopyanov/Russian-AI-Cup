package moveevaluator;

import helpers.DistanceCalculator;
import helpers.Helper;
import helpers.WeightFunctions;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Bonus;
import model.BonusType;
import model.Direction;
import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

public class GoToBonus extends MoveEvaluatorImpl
{
	
	private static class BonusComparatorByImportance implements
	        Comparator<Bonus>
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
			if (BonusType.FIELD_RATION.equals(o1.getType())
			        && BonusType.MEDIKIT.equals(o2.getType()))
			{
				return 1;
			}
			if (BonusType.FIELD_RATION.equals(o1.getType())
			        && BonusType.GRENADE.equals(o2.getType()))
			{
				return 1;
			}
			if (BonusType.GRENADE.equals(o1.getType())
			        && BonusType.MEDIKIT.equals(o2.getType()))
			{
				return Helper.INSTANCE.findWoundedTeammates(world).isEmpty() ? -1
				        : 1;
			}
			return DistanceCalculator.INSTANCE.getDistance(self.getX(),
			        self.getY(), o1.getX(), o1.getY(), world)
			        - DistanceCalculator.INSTANCE.getDistance(self.getX(),
			                self.getY(), o2.getX(), o2.getY(), world);
		}
		
	}
	
	public static final GoToBonus INSTANCE = new GoToBonus();
	
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
			if ((BonusType.FIELD_RATION.equals(bonus.getType()) && !self
			        .isHoldingFieldRation())
			        || (BonusType.GRENADE.equals(bonus.getType()) && !self
			                .isHoldingGrenade())
			        || (BonusType.MEDIKIT.equals(bonus.getType()) && !self
			                .isHoldingMedikit()))
			{
				pickableBonuses.add(bonus);
			}
			
		}
		if (pickableBonuses.isEmpty())
		{
			return;
		}
		Collections.sort(pickableBonuses, new BonusComparatorByImportance(self,
		        world, game));
		
		for (int i = 0; i < pickableBonuses.size(); i++)
		{
			Bonus bestBonus = pickableBonuses.get(i);
			for (Direction direction : Helper.INSTANCE.getDirectionForPath(
			        self.getX(), self.getY(), bestBonus.getX(),
			        bestBonus.getY(), world))
			{
				MoveEvaluation moveEvaluation = MoveEvaluation.move(direction);
				MoveEvaluations.INSTANCE.addMoveEvaluation(moveEvaluation,
				        WeightFunctions.bonusWeightFunction(i));
			}
		}
	}
}

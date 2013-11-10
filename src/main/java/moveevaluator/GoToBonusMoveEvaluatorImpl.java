package moveevaluator;

import helpers.Constants;
import helpers.Helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Bonus;
import model.BonusType;
import model.Game;
import model.Trooper;
import model.World;
import trooperstrategy.MoveEvaluation;

public class GoToBonusMoveEvaluatorImpl extends MoveEvaluatorImpl
{
	
	private static class BonusComparatorByImportance implements
	        Comparator<Bonus>
	{
		
		private Trooper self;
		private World world;
		private Game game;
		
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
			return Helper.INSTANCE.getDistance(self.getX(), self.getY(),
			        o1.getX(), o1.getY())
			        - Helper.INSTANCE.getDistance(self.getX(), self.getY(),
			                o2.getX(), o2.getY());
		}
		
		public void init(Trooper self, World world, Game game)
		{
			this.self = self;
			this.world = world;
			this.game = game;
		}
		
	}
	
	private static BonusComparatorByImportance BONUS_COMPARATOR = new BonusComparatorByImportance();
	
	@Override
	public void evaluate(Trooper self, World world, Game game, List<MoveEvaluation> moveEvaluations)
	{
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
		BONUS_COMPARATOR.init(self, world, game);
		Collections.sort(pickableBonuses, BONUS_COMPARATOR);
		Bonus bestBonus = pickableBonuses.get(0);
		
		MoveEvaluation moveEvaluation = MoveEvaluation.move(Helper.INSTANCE
		        .getDirectionForPath(self.getX(), self.getY(),
		                bestBonus.getX(), bestBonus.getY()));
		int pos = moveEvaluations.indexOf(moveEvaluation);
		if (pos == -1)
		{
			moveEvaluations.add(moveEvaluation);
		}
		else
		{
			moveEvaluation = moveEvaluations.get(pos);
		}
		moveEvaluation.setEvaluation(moveEvaluation.getEvaluation()
		        + Constants.BONUS_MOVE_EVALUATION);
	}
	
}

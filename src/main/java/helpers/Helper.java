package helpers;

import java.util.*;

import model.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class Helper
{
    public static class TrooperDistanceFilter implements Predicate<Trooper>
    {
        private Cell cell;
        private int distance;

        @Override
        public boolean apply(Trooper arg0)
        {
            return DistanceCalculator.INSTANCE.getDistance(cell, new Cell(arg0.getX(), arg0.getY())) < distance;
        }

        public void reset(Cell cell, int distance)
        {
            this.cell = cell;
            this.distance = distance;
        }
    }

    private static final Comparator<Trooper> LEADER_QUALITIES_COMPARATOR = new Comparator<Trooper>()
    {
        private final List<TrooperType> LEADER_QUALITIES = Arrays.asList(TrooperType.COMMANDER, TrooperType.SOLDIER,
                TrooperType.FIELD_MEDIC, TrooperType.SNIPER, TrooperType.SCOUT);

        @Override
        public int compare(Trooper o1, Trooper o2)
        {
            return LEADER_QUALITIES.indexOf(o1.getType()) - LEADER_QUALITIES.indexOf(o2.getType());
        }
    };

    public static TrooperDistanceFilter TROOPER_DISTANCE_FILTER = new TrooperDistanceFilter();

    public static Helper INSTANCE = new Helper();

    private TrooperType firstTrooperToMove;

    public List<Trooper> findEnemies(World world)
    {
        List<Trooper> result = Lists.newArrayList();
        for (Trooper trooper : world.getTroopers())
        {
            if (!trooper.isTeammate())
            {
                result.add(trooper);
            }
        }
        return result;
    }

    public Cell findPassableClosestCell(Cell cell, World world)
    {
        int xDirection = cell.getX() < world.getWidth() / 2 ? -1 : 1;
        int yDirection = cell.getY() < world.getHeight() / 2 ? -1 : 1;
        int basex = cell.getX();
        int basey = cell.getY();
        int x = basex;
        int y = basey;

        int distance = 1;
        int i = 0;
        while (!CellType.FREE.equals(world.getCells()[x][y]))
        {
            if (i < distance)
            {
                x = basex + i % distance * xDirection;
                y = basey + distance * yDirection;
            }
            else
            {
                x = basex + distance * xDirection;
                y = basey + (i - distance) % distance * yDirection;
            }

            i++;
            if (i == distance * 2 + 1)
            {
                i = 0;
                distance++;
            }
        }
        return new Cell(x, y);
    }

    public List<Trooper> findSquad(World world)
    {
        List<Trooper> result = new ArrayList<Trooper>();
        for (Trooper trooper : world.getTroopers())
        {
            if (trooper.isTeammate())
            {
                result.add(trooper);
            }
        }
        return result;
    }

    public Trooper findSquadLeader(World world)
    {
        List<Trooper> squad = findSquad(world);
        Collections.sort(squad, LEADER_QUALITIES_COMPARATOR);
        return squad.get(0);
    }

    public List<Trooper> findVisibleEnemies(Trooper self, World world)
    {
        List<Trooper> result = Lists.newArrayList();
        for (Trooper trooper : world.getTroopers())
        {
            if (!trooper.isTeammate()
                    && world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(),
                            trooper.getX(), trooper.getY(), trooper.getStance()))
            {
                result.add(trooper);
            }
        }
        return result;
    }

    public List<Trooper> findWoundedTeammates(World world)
    {
        List<Trooper> result = new LinkedList<Trooper>();
        for (Trooper trooper : world.getTroopers())
        {
            if (trooper.isTeammate() && trooper.getHitpoints() <= Constants.WOUNDED_HP)
            {
                result.add(trooper);
            }
        }
        return result;
    }

    public List<Direction> getDirectionForPath(int xfrom, int yfrom, int xto, int yto)
    {
        List<Direction> result = Lists.newArrayList();
        int distanceFromHere = DistanceCalculator.INSTANCE.getDistance(xfrom, yfrom, xto, yto);
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom + 1, yfrom, xto, yto) + 1)
        {
            result.add(Direction.EAST);
        }
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom - 1, yfrom, xto, yto) + 1)
        {
            result.add(Direction.WEST);
        }
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom, yfrom - 1, xto, yto) + 1)
        {
            result.add(Direction.NORTH);
        }
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom, yfrom + 1, xto, yto) + 1)
        {
            result.add(Direction.SOUTH);
        }
        return result;
    }

    public TrooperType getFirstTrooperToMove()
    {
        return firstTrooperToMove;
    }

    public int getMoveCost(Trooper self, Game game)
    {
        switch (self.getStance())
        {
        case KNEELING:
            return game.getKneelingMoveCost();
        case STANDING:
            return game.getStandingMoveCost();
        case PRONE:
            return game.getProneMoveCost();
        }
        throw new RuntimeException("WTF? Helper.getMoveCost");
    }

    public void init(Trooper trooper)
    {
        this.firstTrooperToMove = trooper.getType();
    }
}
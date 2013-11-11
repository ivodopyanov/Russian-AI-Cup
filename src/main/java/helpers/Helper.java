package helpers;

import java.util.*;

import model.*;

public class Helper
{
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

    public static Helper INSTANCE = new Helper();

    private TrooperType firstTrooperToMove;

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

    public Direction getDirectionForPath(int xfrom, int yfrom, int xto, int yto)
    {
        int distanceFromHere = DistanceCalculator.INSTANCE.getDistance(xfrom, yfrom, xto, yto);
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom + 1, yfrom, xto, yto) + 1)
        {
            return Direction.EAST;
        }
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom - 1, yfrom, xto, yto) + 1)
        {
            return Direction.WEST;
        }
        if (distanceFromHere == DistanceCalculator.INSTANCE.getDistance(xfrom, yfrom - 1, xto, yto) + 1)
        {
            return Direction.NORTH;
        }
        return Direction.SOUTH;
    }

    public TrooperType getFirstTrooperToMove()
    {
        return firstTrooperToMove;
    }

    public void init(Trooper trooper)
    {
        this.firstTrooperToMove = trooper.getType();
    }
}
import java.util.*;

import model.*;

public class Helper
{
    private static final Comparator<Trooper> LEADER_QUALITIES_COMPARATOR = new Comparator<Trooper>()
    {

        private final List<TrooperType> LEADER_QUALITIES = Arrays.asList(TrooperType.SOLDIER, TrooperType.COMMANDER,
                TrooperType.FIELD_MEDIC, TrooperType.SNIPER, TrooperType.SCOUT);

        @Override
        public int compare(Trooper o1, Trooper o2)
        {
            return LEADER_QUALITIES.indexOf(o1.getType()) - LEADER_QUALITIES.indexOf(o2.getType());
        }
    };

    public static Helper INSTANCE = new Helper();

    private TrooperType firstTrooperToMove;

    private boolean initialized = false;

    public Cell centerOfTrooperGroup(Collection<Trooper> group)
    {
        if (group.isEmpty())
        {
            return null;
        }
        int x = 0;
        int y = 0;
        for (Trooper trooper : group)
        {
            x += trooper.getX();
            y += trooper.getY();
        }
        return Cell.create(x / group.size(), y / group.size());
    }

    public double distance(int x1, int y1, int x2, int y2)
    {
        double deltax = x2 - x1;
        double deltay = y2 - y1;
        return Math.sqrt(deltax * deltax + deltay * deltay);
    }

    public List<Trooper> findEnemies(World world)
    {
        List<Trooper> result = new ArrayList<Trooper>();
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
        if (y >= world.getHeight())
        {
            y = world.getHeight() - 1;
            yDirection = -1;
        }
        else if (y < 0)
        {
            y = 0;
            yDirection = 1;
        }
        if (x >= world.getWidth())
        {
            x = world.getWidth() - 1;
            xDirection = -1;
        }
        else if (x < 0)
        {
            x = 0;
            xDirection = 1;
        }

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
        return Cell.create(x, y);
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

    public List<Trooper> findVisibleEnemies(Trooper self, World world, double range)
    {
        List<Trooper> result = new ArrayList<Trooper>();
        for (Trooper trooper : world.getTroopers())
        {
            if (!trooper.isTeammate()
                    && world.isVisible(range, self.getX(), self.getY(), self.getStance(), trooper.getX(),
                            trooper.getY(), trooper.getStance()))
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

    public Direction getDirectionForNeighbours(Cell pivot, Cell neighbour)
    {
        if (pivot.getY() == neighbour.getY())
        {
            if (pivot.getX() == neighbour.getX() - 1)
            {
                return Direction.EAST;
            }
            else if (pivot.getX() == neighbour.getX() + 1)
            {
                return Direction.WEST;
            }
            else
            {
                return null;
            }
        }
        else if (pivot.getX() == neighbour.getX())
        {
            if (pivot.getY() == neighbour.getY() - 1)
            {
                return Direction.SOUTH;
            }
            else if (pivot.getY() == neighbour.getY() + 1)
            {
                return Direction.NORTH;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }

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
        initialized = true;
    }

    public boolean isInitialized()
    {
        return initialized;
    }
}

public class Cell
{
    private static Cell[][] CACHE = new Cell[64][44];//Чтобы иметь возможность создавать ячейки с отрицательными координатами или ячейки границы

    public static Cell create(int x, int y)
    {
        if (CACHE[x + 32][y + 22] != null)
        {
            return CACHE[x + 32][y + 22];
        }
        Cell result = new Cell(x, y);
        CACHE[x + 32][y + 22] = result;
        return result;
    }

    private final int x;
    private final int y;

    private Cell(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Cell))
        {
            return false;
        }
        Cell cell = (Cell)o;
        return getX() == cell.getX() && getY() == cell.getY();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public int hashCode()
    {
        return x + y;
    }

    @Override
    public String toString()
    {
        return String.format("[%d, %d]", x, y);
    }
}

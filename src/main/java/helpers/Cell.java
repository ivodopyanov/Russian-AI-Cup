package helpers;

public class Cell
{
    private final int x;
    private final int y;

    public Cell(int x, int y)
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
}

import model.Trooper;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 23 нояб. 2013 г.
 *
 */
public class DistanceCalcContext
{
    private final Trooper trooper;
    private final int startAP;
    private final int turnIndex;
    private final Cell start;

    public DistanceCalcContext(Trooper trooper, int startAP, int turnIndex, Cell start)
    {
        this.trooper = trooper;
        this.startAP = startAP;
        this.turnIndex = turnIndex;
        this.start = start;
    }

    public Cell getStart()
    {
        return start;
    }

    public int getStartAP()
    {
        return startAP;
    }

    public Trooper getTrooper()
    {
        return trooper;
    }

    public int getTurnIndex()
    {
        return turnIndex;
    }
}
import model.Trooper;

/**
 * 
 */

public class PathNode
{
    private final Cell cell;
    private final int turnIndex;
    private final PathNode prevPathNode;
    private final int currentAP;
    private final int spentAP;
    private final Trooper trooper;

    public PathNode(Cell cell, int turnIndex, PathNode prevPathNode, int currentAP, int spentAP, Trooper trooper)
    {
        this.cell = cell;
        this.turnIndex = turnIndex;
        this.prevPathNode = prevPathNode;
        this.currentAP = currentAP;
        this.spentAP = spentAP;
        this.trooper = trooper;
    }

    public PathNode(DistanceCalcContext context)
    {
        this(context.getStart(), context.getTurnIndex(), null, context.getStartAP(), 0, context.getTrooper());
    }

    public Cell getCell()
    {
        return cell;
    }

    public int getCurrentAP()
    {
        return currentAP;
    }

    public PathNode getPrevPathNode()
    {
        return prevPathNode;
    }

    public int getSpentAP()
    {
        return spentAP;
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
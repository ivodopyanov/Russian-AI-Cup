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

    public PathNode(Cell cell, int turnIndex, PathNode prevPathNode, int currentAP, int spentAP)
    {
        this.cell = cell;
        this.turnIndex = turnIndex;
        this.prevPathNode = prevPathNode;
        this.currentAP = currentAP;
        this.spentAP = spentAP;
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

    public int getTurnIndex()
    {
        return turnIndex;
    }
}
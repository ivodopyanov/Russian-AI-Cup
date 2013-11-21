import model.Trooper;

/**
 * 
 */

/**
 * @author ivodopyanov
 * @since 21 нояб. 2013 г.
 *
 */
public class TrooperCondition
{
    private Trooper trooper;
    private boolean isBeingHealed = false;
    private boolean isBeingShot = false;
    private int turn;

    public Trooper getTrooper()
    {
        return trooper;
    }

    public int getTurn()
    {
        return turn;
    }

    public boolean isBeingHealed()
    {
        return isBeingHealed;
    }

    public boolean isBeingShot()
    {
        return isBeingShot;
    }

    public void setBeingHealed(boolean isBeingHealed)
    {
        this.isBeingHealed = isBeingHealed;
    }

    public void setBeingShot(boolean isBeingShot)
    {
        this.isBeingShot = isBeingShot;
    }

    public void setTrooper(Trooper trooper)
    {
        this.trooper = trooper;
    }

    public void setTurn(int turn)
    {
        this.turn = turn;
    }
}
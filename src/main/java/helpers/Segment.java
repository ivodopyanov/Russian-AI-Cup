package helpers;

public class Segment
{
	
	private final Cell start;
	private final Cell end;
	
	public Segment(Cell start, Cell end)
	{
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Segment))
		{
			return false;
		}
		Segment segment = (Segment) o;
		return (getStart().equals(segment.getStart()) && getEnd().equals(
		        segment.getEnd()))
		        || (getStart().equals(segment.getEnd()) && getEnd().equals(
		                segment.getStart()));
	}
	
	public Cell getEnd()
	{
		return end;
	}
	
	public Cell getStart()
	{
		return start;
	}
	
	@Override
	public int hashCode()
	{
		return start.hashCode() + end.hashCode();
	}
}

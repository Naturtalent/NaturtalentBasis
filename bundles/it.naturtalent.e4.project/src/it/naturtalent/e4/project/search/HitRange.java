package it.naturtalent.e4.project.search;

/**
 * @author Markus Gebhard
 */
public class HitRange
{

	private final int hitLength;

	private final int startIndex;

	public HitRange(int startIndex, int hitLength)
	{
		this.startIndex = startIndex;
		this.hitLength = hitLength;
	}

	public int getHitLength()
	{
		return hitLength;
	}

	public int getStartIndex()
	{
		return startIndex;
	}
}
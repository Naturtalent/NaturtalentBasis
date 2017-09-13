package it.naturtalent.e4.project.search;

import it.naturtalent.e4.project.search.textcomponents.ITextComponent;



/**
 * @author Markus Gebhard
 */
public class SearchHit
{

	private ITextComponent textComponent;

	protected String label;

	private final HitRange hitRange;

	public SearchHit(String label, ITextComponent textComponent,
			HitRange hitRange)
	{
		this.label = label;
		this.textComponent = textComponent;
		this.hitRange = hitRange;
	}

	public String getLabel()
	{
		return label;
	}

	public ITextComponent getTextComponent()
	{
		return textComponent;
	}

	public MatchedText getMatchedText()
	{
		return new MatchedText(textComponent, hitRange.getStartIndex(),
				hitRange.getHitLength());
	}
}
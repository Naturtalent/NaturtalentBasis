package it.naturtalent.e4.project.search;

import it.naturtalent.e4.project.search.textcomponents.ITextComponent;

import org.apache.commons.lang3.StringUtils;



/**
 * @author Markus Gebhard
 */
public class TextSearcher
{
	public static HitRange getFirstHit(ITextComponent text,
			SearchOptions searchOptions)
	{
		return getFirstHit(text, searchOptions, 0);
	}

	private static HitRange getFirstHit(ITextComponent text,
			SearchOptions searchOptions, int fromIndex)
	{
		int startIndex = -1;
		if (searchOptions.isCaseSensitive())
		{
			startIndex = text.getText().indexOf(
					searchOptions.getSearchPattern(), fromIndex);
		}
		else
		{
			startIndex = text
					.getText()
					.toLowerCase()
					.indexOf(searchOptions.getSearchPattern().toLowerCase(),
							fromIndex);
		}
		if (startIndex == -1)
		{
			return null;
		}

		if (searchOptions.isWholeWordOnly())
		{
			int lastCharacterIndex = startIndex
					+ searchOptions.getSearchPattern().length() - 1;
			if (!TextUtilities.isWholeWord(text.getText(), startIndex,
					lastCharacterIndex))
			{
				return getFirstHit(text, searchOptions, fromIndex + 1);
			}
		}
		return new HitRange(startIndex, searchOptions.getSearchPattern()
				.length());
	}

}
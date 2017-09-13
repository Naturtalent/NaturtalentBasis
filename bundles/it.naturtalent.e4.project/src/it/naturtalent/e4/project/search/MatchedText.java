package it.naturtalent.e4.project.search;

import it.naturtalent.e4.project.search.textcomponents.ComponentPath;
import it.naturtalent.e4.project.search.textcomponents.ITextComponent;


/**
 * @author Markus Gebhard
 */
public class MatchedText
{
	private static final String DOTS = "..."; //$NON-NLS-1$

	private int hitLength;

	private int hitStartIndex;

	private ITextComponent textComponent;

	private String text;

	public MatchedText(ITextComponent textComponent, int hitStartIndex,
			int hitLength)
	{
		this(textComponent, textComponent.getText(), hitStartIndex, hitLength);
	}

	public MatchedText(ITextComponent textComponent, String text,
			int hitStartIndex, int hitLength)
	{
		this.textComponent = textComponent;
		this.text = text;
		this.hitStartIndex = hitStartIndex;
		this.hitLength = hitLength;
	}

	public int getHitLength()
	{
		return hitLength;
	}

	public int getHitStartIndex()
	{
		return hitStartIndex;
	}

	public String getComponentType()
	{
		return textComponent.getTypeLabel();
	}

	public String getComponentPathDescription()
	{
		ComponentPath path = textComponent.getPath();
		String[] pathComponents = path.getPathComponents();
		return TextUtilities.concat(pathComponents, " -> "); //$NON-NLS-1$
	}

	public String getTextString()
	{
		return text;
	}

	public MatchedText getCropped(int maxLength)
	{
		String croppedText = getTextString();
		croppedText = croppedText.replace('\r', ' ');
		croppedText = croppedText.replace('\n', ' ');
		int hitStartIndex = getHitStartIndex();
		if (croppedText.length() > maxLength)
		{
			int removeCount = croppedText.length() - maxLength;
			int leftCroppableCount = getHitStartIndex();
			int rightCroppableCount = croppedText.length() - getHitStartIndex()
					- getHitLength();

			int leftCrop = 0;
			int rightCrop = 0;
			while (removeCount > 0
					&& (leftCroppableCount > 0 || rightCroppableCount > 0))
			{
				if (leftCroppableCount > rightCroppableCount
						&& leftCroppableCount > 0)
				{
					++leftCrop;
					--leftCroppableCount;
					--removeCount;
				}
				else
				{
					++rightCrop;
					--rightCroppableCount;
					--removeCount;
				}
			}
			if (leftCrop > 0 && rightCrop > 0)
			{
				croppedText = DOTS
						+ croppedText.substring(leftCrop, croppedText.length()
								- rightCrop) + DOTS;
				hitStartIndex += 3;
				hitStartIndex -= leftCrop;
			}
			else if (leftCrop > 0)
			{
				croppedText = DOTS + croppedText.substring(leftCrop);
				hitStartIndex += 3;
				hitStartIndex -= leftCrop;
			}
			else
			{
				croppedText = croppedText.substring(0, croppedText.length()
						- rightCrop)
						+ DOTS;
			}
		}
		return new MatchedText(this.textComponent, croppedText, hitStartIndex,
				this.hitLength);
	}
}
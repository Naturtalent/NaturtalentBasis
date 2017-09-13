package it.naturtalent.e4.project.search;

/**
 * @author Markus Gebhard
 */
public class TextUtilities
{

	public static boolean isWholeWord(String text, int firstCharacterIndex,
			int lastCharacterIndex)
	{
		if (firstCharacterIndex > 0)
		{
			char characterBeforeWord = text.charAt(firstCharacterIndex - 1);
			if (!isWordSeparator(characterBeforeWord))
			{
				return false;
			}
		}
		if (lastCharacterIndex < text.length() - 1)
		{
			char characterBehindWord = text.charAt(lastCharacterIndex + 1);
			if (!isWordSeparator(characterBehindWord))
			{
				return false;
			}
		}

		return true;
	}

	private static boolean isWordSeparator(char character)
	{
		return !Character.isLetter(character);
	}

	public static String concat(String[] texts, String separator)
	{
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < texts.length; i++)
		{
			if (i > 0)
			{
				result.append(separator);
			}
			result.append(texts[i]);
		}
		return result.toString();
	}

	public static String removeAmps(String text)
	{
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); ++i)
		{
			if (text.charAt(i) != '&')
			{
				result.append(text.charAt(i));
			}
		}
		return result.toString();
	}

	public static String removeAcceleratorKey(String text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char ch = text.charAt(i);
			if (ch == 9)
			{
				return text.substring(0, i);
			}
		}
		return text;
	}
}
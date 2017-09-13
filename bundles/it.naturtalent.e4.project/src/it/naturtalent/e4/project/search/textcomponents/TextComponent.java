package it.naturtalent.e4.project.search.textcomponents;

import it.naturtalent.e4.project.search.Ensure;



public class TextComponent implements ITextComponent
{
	private final String text;

	private final TextComponentType type;

	private final ComponentPath path;

	private final String typeLabel;

	public TextComponent(TextComponentType type, String typeLabel, String text,
			ComponentPath path)
	{
		Ensure.ensureArgumentNotNull(type);
		Ensure.ensureArgumentNotNull(typeLabel);
		Ensure.ensureArgumentNotNull(text);
		Ensure.ensureArgumentNotNull(path);

		this.type = type;
		this.typeLabel = typeLabel;
		this.path = path.getClone();
		this.text = text.trim();
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof TextComponent))
		{
			return false;
		}
		TextComponent other = (TextComponent) obj;
		return type.equals(other.type) && typeLabel.equals(other.typeLabel)
				&& text.equals(other.text) && path.equals(other.path);
	}

	public String getText()
	{
		return text;
	}

	public boolean isTextEmpty()
	{
		return text.length() == 0;
	}

	public String getTypeLabel()
	{
		return typeLabel;
	}

	public TextComponentType getType()
	{
		return type;
	}

	public ComponentPath getPath()
	{
		return path;
	}
}
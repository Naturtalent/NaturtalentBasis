package it.naturtalent.e4.project.search.textcomponents;

public interface ITextComponent
{

	public boolean equals(Object obj);

	public String getText();

	public boolean isTextEmpty();

	public String getTypeLabel();

	public ComponentPath getPath();

	public TextComponentType getType();
}
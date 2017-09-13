package it.naturtalent.e4.project.search.textcomponents;

/**
 * @author Markus Gebhard
 */
public abstract class TextComponentType
{

	private final String name;

	public static final TextComponentType CHECKBOX_SELECTED = new TextComponentType(
			"Checkbox selected") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitCheckboxSelected(this);
		}
	};

	public static final TextComponentType CHECKBOX_NOT_SELECTED = new TextComponentType(
			"Checkbox not selected") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitCheckboxNotSelected(this);
		}
	};

	public static final TextComponentType RADIO_SELECTED = new TextComponentType(
			"Radio selected") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitRadioSelected(this);
		}
	};

	public static final TextComponentType RADIO_NOT_SELECTED = new TextComponentType(
			"Radio not selected") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitRadioNotSelected(this);
		}
	};

	public static final TextComponentType TAB_ITEM = new TextComponentType(
			"Tab item") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitTabItem(this);
		}
	};

	public static final TextComponentType TOOLTIP = new TextComponentType(
			"Tooltip") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitTooltip(this);
		}
	};

	public static final TextComponentType MENU = new TextComponentType("Menu") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitMenu(this);
		}
	};

	public static final TextComponentType MENU_ITEM = new TextComponentType(
			"Menu item") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitMenuItem(this);
		}
	};

	public static final TextComponentType LABEL = new TextComponentType("Label") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitLabel(this);
		}
	};

	public static final TextComponentType BUTTON = new TextComponentType(
			"Button") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitButton(this);
		}
	};

	public static final TextComponentType TEXT = new TextComponentType("Text") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitText(this);
		}
	};

	public static final TextComponentType COMBO_ITEM = new TextComponentType(
			"Combo item") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitComboItem(this);
		}
	};

	public static final TextComponentType LIST_ITEM = new TextComponentType(
			"List item") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitListItem(this);
		}
	};

	public static final TextComponentType STYLED_TEXT = new TextComponentType(
			"Styled text") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitStyledText(this);
		}
	};

	public static final TextComponentType TREE_ITEM = new TextComponentType(
			"Tree Item") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitTreeItem(this);
		}
	};

	public static final TextComponentType GROUP = new TextComponentType("Group") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitGroup(this);
		}
	};

	public static final TextComponentType OTHER = new TextComponentType("Other") { //$NON-NLS-1$
		public void accept(ITextComponentTypeVisitor visitor)
		{
			visitor.visitOther(this);
		}
	};

	private TextComponentType(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public abstract void accept(ITextComponentTypeVisitor visitor);

}
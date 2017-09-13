package it.naturtalent.e4.project.search.textcomponents;

/**
 * @author Markus Gebhard
 */
public interface ITextComponentTypeVisitor {

  public void visitGroup(TextComponentType type);

  public void visitText(TextComponentType type);

  public void visitStyledText(TextComponentType type);

  public void visitButton(TextComponentType type);

  public void visitCheckboxSelected(TextComponentType type);

  public void visitCheckboxNotSelected(TextComponentType type);

  public void visitRadioSelected(TextComponentType type);

  public void visitRadioNotSelected(TextComponentType type);

  public void visitLabel(TextComponentType type);

  public void visitMenu(TextComponentType type);

  public void visitMenuItem(TextComponentType type);

  public void visitTabItem(TextComponentType type);

  public void visitTreeItem(TextComponentType type);

  public void visitListItem(TextComponentType type);

  public void visitComboItem(TextComponentType type);

  public void visitTooltip(TextComponentType type);

  public void visitOther(TextComponentType type);
}
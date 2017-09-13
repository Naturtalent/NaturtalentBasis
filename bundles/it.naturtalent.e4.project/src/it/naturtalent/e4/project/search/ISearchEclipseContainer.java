package it.naturtalent.e4.project.search;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

/**
 * @author Markus Gebhard
 */
public interface ISearchEclipseContainer
{

	public void startSearch();

	public void updateStartSearchEnabled();

	public IWorkbench getWorkbench();

	public Shell getShell();

	public Shell getParentShell();

}

package it.naturtalent.e4.project.expimp.dialogs;

import java.text.Collator;
import java.util.Locale;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public abstract class AbstractTableSorter extends ViewerComparator
{
	public static final int ASC = 1;

	public static final int NONE = 0;

	public static final int DESC = -1;

	private int direction = 0;

	private TableViewerColumn column;

	private TableViewer viewer;

	public AbstractTableSorter(TableViewer viewer, TableViewerColumn column)
	{
		this.viewer = viewer;
		this.column = column;
		this.column.getColumn().addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (AbstractTableSorter.this.viewer.getComparator() != null)
				{
					if (AbstractTableSorter.this.viewer.getComparator() == AbstractTableSorter.this)
					{
						int tdirection = AbstractTableSorter.this.direction;

						if (tdirection == ASC)
						{
							setSorter(AbstractTableSorter.this, DESC);
						}
						else if (tdirection == DESC)
						{
							setSorter(AbstractTableSorter.this, NONE);
						}
					}
					else
					{
						setSorter(AbstractTableSorter.this, ASC);
					}
				}
				else
				{
					setSorter(AbstractTableSorter.this, ASC);
				}
			}
		});
	}

	public void setSorter(AbstractTableSorter sorter, int direction)
	{
		if (direction == NONE)
		{
			column.getColumn().getParent().setSortColumn(null);
			column.getColumn().getParent().setSortDirection(SWT.NONE);
			viewer.setComparator(null);
		}
		else
		{
			column.getColumn().getParent().setSortColumn(column.getColumn());
			sorter.direction = direction;

			if (direction == ASC)
			{
				column.getColumn().getParent().setSortDirection(SWT.DOWN);
			}
			else
			{
				column.getColumn().getParent().setSortDirection(SWT.UP);
			}

			if (viewer.getComparator() == sorter)
			{
				viewer.refresh();
			}
			else
			{
				viewer.setComparator(sorter);
			}

		}
	}

	public int compare(Viewer viewer, Object e1, Object e2)
	{
		return direction * doCompare(viewer, e1, e2);
	}

	protected abstract int doCompare(Viewer viewer, Object e1, Object e2);

}

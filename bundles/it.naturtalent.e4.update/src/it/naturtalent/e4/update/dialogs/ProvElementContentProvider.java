package it.naturtalent.e4.update.dialogs;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.ui.model.ProvElement;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.ui.progress.PendingUpdateAdapter;

public class ProvElementContentProvider implements ITreeContentProvider
{

	private boolean fetchInBackground = false;
	Viewer viewer;
	private Job fetchJob;
	// family is used by test cases
	Object fetchFamily = new Object();
	
	
	@Override
	public void dispose()
	{
		viewer = null;
		if (fetchJob != null)
		{
			fetchJob.cancel();
			fetchJob = null;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = viewer;
		if (fetchJob != null)
		{
			fetchJob.cancel();
			fetchJob = null;
		}
	}

	@Override
	public Object[] getElements(final Object input)
	{
		// Simple deferred fetch handling for table viewers
		if (fetchInBackground && input instanceof IDeferredWorkbenchAdapter
				&& viewer instanceof AbstractTableViewer)
		{
			final Display display = viewer.getControl().getDisplay();
			final Object pending = new PendingUpdateAdapter();
			if (fetchJob != null)
				fetchJob.cancel();
			fetchJob = new Job("Fetching Elements")
			{
				protected IStatus run(final IProgressMonitor monitor)
				{
					IDeferredWorkbenchAdapter parent = (IDeferredWorkbenchAdapter) input;
					final ArrayList<Object> children = new ArrayList<Object>();
					parent.fetchDeferredChildren(parent,
							new IElementCollector()
							{
								public void add(Object element,
										IProgressMonitor mon)
								{
									if (mon.isCanceled())
										return;
									children.add(element);
								}

								public void add(Object[] elements,
										IProgressMonitor mon)
								{
									if (mon.isCanceled())
										return;
									children.addAll(Arrays.asList(elements));
								}

								public void done()
								{
									// nothing special to do
								}

							}, monitor);
					if (!monitor.isCanceled())
					{
						display.asyncExec(new Runnable()
						{
							public void run()
							{
								AbstractTableViewer tableViewer = (AbstractTableViewer) viewer;
								if (monitor.isCanceled()
										|| tableViewer == null
										|| tableViewer.getControl()
												.isDisposed())
									return;
								tableViewer.getControl().setRedraw(false);
								tableViewer.remove(pending);
								tableViewer.add(children.toArray());
								finishedFetchingElements(input);
								tableViewer.getControl().setRedraw(true);
							}
						});
					}
					return Status.OK_STATUS;
				}

				public boolean belongsTo(Object family)
				{
					return family == fetchFamily;
				}

			};
			fetchJob.schedule();
			return new Object[]
				{ pending };
		}
		Object[] elements = getChildren(input);
		finishedFetchingElements(input);
		return elements;
	}
	
	protected void finishedFetchingElements(Object parent) {
		// do nothing
	}

	@Override
	public Object[] getChildren(Object parent)
	{
		if (parent instanceof ProvElement)
		{
			return ((ProvElement) parent).getChildren(parent);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof ProvElement)
			return ((ProvElement) element).hasChildren(element);
		return false;
	}

}

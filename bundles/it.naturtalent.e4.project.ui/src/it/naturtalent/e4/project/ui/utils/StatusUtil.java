package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Activator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * Utility class to create status objects.
 * 
 * PRIVATE This class is an internal implementation class and should not be
 * referenced or sub-classed outside of the workbench
 * 
 * @since 3.0
 */
public class StatusUtil
{

	/**
	 * Answer a flat collection of the passed status and its recursive children
	 */
	protected static List flatten(IStatus aStatus)
	{
		List result = new ArrayList();

		if (aStatus.isMultiStatus())
		{
			IStatus[] children = aStatus.getChildren();
			for (int i = 0; i < children.length; i++)
			{
				IStatus currentChild = children[i];
				if (currentChild.isMultiStatus())
				{
					Iterator childStatiiEnum = flatten(currentChild).iterator();
					while (childStatiiEnum.hasNext())
					{
						result.add(childStatiiEnum.next());
					}
				}
				else
				{
					result.add(currentChild);
				}
			}
		}
		else
		{
			result.add(aStatus);
		}

		return result;
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 */
	protected static IStatus newStatus(IStatus[] stati, String message,
			Throwable exception)
	{

		if (message == null || message.trim().length() == 0)
		{
			throw new IllegalArgumentException();
		}
		return new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, stati,
				message, exception);
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * 
	 * @param severity
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(int severity, String message,
			Throwable exception)
	{

		String statusMessage = message;
		if (message == null || message.trim().length() == 0)
		{
			if (exception == null)
			{
				throw new IllegalArgumentException();
			}
			else if (exception.getMessage() == null)
			{
				statusMessage = exception.toString();
			}
			else
			{
				statusMessage = exception.getMessage();
			}
		}

		return new Status(severity, Activator.PLUGIN_ID, severity,
				statusMessage, exception);
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * 
	 * @param children
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(List children, String message,
			Throwable exception)
	{

		List flatStatusCollection = new ArrayList();
		Iterator iter = children.iterator();
		while (iter.hasNext())
		{
			IStatus currentStatus = (IStatus) iter.next();
			Iterator childrenIter = flatten(currentStatus).iterator();
			while (childrenIter.hasNext())
			{
				flatStatusCollection.add(childrenIter.next());
			}
		}

		IStatus[] stati = new IStatus[flatStatusCollection.size()];
		flatStatusCollection.toArray(stati);
		return newStatus(stati, message, exception);
	}
	
	public static IStatus newStatus(String pluginId, Throwable exception)
	{
		return newStatus(pluginId, getLocalizedMessage(exception), exception);
	}

	public static IStatus newStatus(String pluginId, String message,
			Throwable exception)
	{
		return new Status(IStatus.ERROR, pluginId, IStatus.OK, message,
				getCause(exception));
	}
    
	public static Throwable getCause(Throwable exception)
	{
		// Figure out which exception should actually be logged -- if the given
		// exception is
		// a wrapper, unwrap it
		Throwable cause = null;
		if (exception != null)
		{
			if (exception instanceof CoreException)
			{
				// Workaround: CoreException contains a cause, but does not
				// actually implement getCause().
				// If we get a CoreException, we need to manually unpack the
				// cause. Otherwise, use
				// the general-purpose mechanism. Remove this branch if
				// CoreException ever implements
				// a correct getCause() method.
				CoreException ce = (CoreException) exception;
				cause = ce.getStatus().getException();
			}
			else
			{
				// use reflect instead of a direct call to getCause(), to allow
				// compilation against JCL Foundation (bug 80053)
				try
				{
					Method causeMethod = exception.getClass().getMethod(
							"getCause", new Class[0]); //$NON-NLS-1$
					Object o = causeMethod.invoke(exception, new Object[0]);
					if (o instanceof Throwable)
					{
						cause = (Throwable) o;
					}
				} catch (NoSuchMethodException e)
				{
					// ignore
				} catch (IllegalArgumentException e)
				{
					// ignore
				} catch (IllegalAccessException e)
				{
					// ignore
				} catch (InvocationTargetException e)
				{
					// ignore
				}
			}

			if (cause == null)
			{
				cause = exception;
			}
		}

		return cause;
	}

    /**
     * Returns a localized message describing the given exception. If the given exception does not
     * have a localized message, this returns the string "An error occurred".
     *
     * @param exception
     * @return
     */
	public static String getLocalizedMessage(Throwable exception)
	{
		String message = exception.getLocalizedMessage();

		if (message != null)
		{
			return message;
		}

		// Workaround for the fact that CoreException does not implement a
		// getLocalizedMessage() method.
		// Remove this branch when and if CoreException implements
		// getLocalizedMessage()
		if (exception instanceof CoreException)
		{
			CoreException ce = (CoreException) exception;
			return ce.getStatus().getMessage();
		}

		return WorkbenchMessages.StatusUtil_errorOccurred;
	}

}

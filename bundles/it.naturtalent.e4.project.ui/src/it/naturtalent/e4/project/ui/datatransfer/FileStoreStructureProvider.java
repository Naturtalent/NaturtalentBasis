package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.ui.Activator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.log.Logger;

/**
 * FileStoreStructureProvider is the structure provider for {@link IFileStore}
 * based file structures.
 * 
 * @since 3.2
 * 
 */
public class FileStoreStructureProvider implements IImportStructureProvider
{
	
	private Log log = LogFactory.getLog(FileStoreStructureProvider.class);


	/**
	 * Holds a singleton instance of this class.
	 */
	public final static FileStoreStructureProvider INSTANCE = new FileStoreStructureProvider();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.datatransfer.IImportStructureProvider#getChildren
	 * (java.lang.Object)
	 */
	public List getChildren(Object element)
	{
		try
		{
			return Arrays.asList(((IFileStore) element).childStores(EFS.NONE,
					new NullProgressMonitor()));
		} catch (CoreException exception)
		{
			logException(exception);
			return new ArrayList();
		}
	}

	/**
	 * Log the exception.
	 * 
	 * @param exception
	 */
	private void logException(CoreException exception)
	{
		log.error(exception.getLocalizedMessage(), exception);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.datatransfer.IImportStructureProvider#getContents
	 * (java.lang.Object)
	 */
	public InputStream getContents(Object element)
	{
		try
		{
			return ((IFileStore) element).openInputStream(EFS.NONE,
					new NullProgressMonitor());
		} catch (CoreException exception)
		{
			logException(exception);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.datatransfer.IImportStructureProvider#getFullPath
	 * (java.lang.Object)
	 */
	public String getFullPath(Object element)
	{
		return ((IFileStore) element).toURI().getSchemeSpecificPart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.datatransfer.IImportStructureProvider#getLabel
	 * (java.lang.Object)
	 */
	public String getLabel(Object element)
	{
		return ((IFileStore) element).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.datatransfer.IImportStructureProvider#isFolder
	 * (java.lang.Object)
	 */
	public boolean isFolder(Object element)
	{
		return ((IFileStore) element).fetchInfo().isDirectory();
	}

}

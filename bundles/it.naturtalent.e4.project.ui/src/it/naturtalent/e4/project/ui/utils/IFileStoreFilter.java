package it.naturtalent.e4.project.ui.utils;

import org.eclipse.core.filesystem.IFileStore;

/**
 * IFileStoreFilter is an interface that defines a filter on file
 * stores.
 * @since 3.2
 *
 */
public interface IFileStoreFilter {
	
	/**
	 * Return whether or not this store is accepted by the receiver.
	 * @param store IFileStore
	 * @return boolean <code>true</code> if this store is accepted.
	 */
	public abstract boolean accept(IFileStore store);

}

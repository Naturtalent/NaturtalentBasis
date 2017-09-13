package it.naturtalent.e4.project.ui.model;

import it.naturtalent.e4.project.ui.WorkbenchImages;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.misc.UIStats;

/**
 * An IWorkbenchAdapter that represents IFiles.
 */
public class WorkbenchFile extends WorkbenchResource
{

	/**
	 * Constant that is used as the key of a session property on IFile objects
	 * to cache the result of doing a proper content type lookup. This will be
	 * set by the ContentTypeDecorator (if enabled) and used instead of the
	 * "guessed" content type in {@link #getBaseImage(IResource)}.
	 * 
	 * @since 3.4
	 */
	public static QualifiedName IMAGE_CACHE_KEY = new QualifiedName(
			WorkbenchPlugin.PI_WORKBENCH, "WorkbenchFileImage"); //$NON-NLS-1$

	/**
	 * Answer the appropriate base image to use for the passed resource,
	 * optionally considering the passed open status as well iff appropriate for
	 * the type of passed resource
	 */
	protected ImageDescriptor getBaseImage(IResource resource)
	{
		IContentType contentType = null;
		// do we need to worry about checking here?
		if (resource instanceof IFile)
		{
			IFile file = (IFile) resource;
			
			String ext = FilenameUtils.getExtension(file.getName());	
			Program prog = Program.findProgram(ext);
			if(prog != null)
			{
				// externes Image
				return ImageDescriptor.createFromImageData(prog
								.getImageData());				
			}
		}
		
		// @issue move IDE specific images
		return WorkbenchImages.getImage(WorkbenchImages.IMG_PROJECT_FILE);
	}
	
	/**
	 * Guess at the content type of the given file based on the filename.
	 * 
	 * @param file
	 *            the file to test
	 * @return the content type, or <code>null</code> if it cannot be
	 *         determined.
	 * @since 3.2
	 */
	public static IContentType guessContentType(IFile file)
	{
		String fileName = file.getName();
		try
		{
			UIStats.start(UIStats.CONTENT_TYPE_LOOKUP, fileName);
			IContentTypeMatcher matcher = file.getProject()
					.getContentTypeMatcher();
			return matcher.findContentTypeFor(fileName);
		} catch (CoreException e)
		{
			return null;
		} finally
		{
			UIStats.end(UIStats.CONTENT_TYPE_LOOKUP, file, fileName);
		}
	}
}

package it.naturtalent.e4.project.ui.model;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * IWorkbenchAdapter adapter for the IWorkspace object.
 */
public class WorkbenchWorkspace extends WorkbenchAdapter {
    /*
     *  (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object o) {
        IWorkspace workspace = (IWorkspace) o;
        return workspace.getRoot().getProjects();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        return null;
    }

    /**
     * getLabel method comment.
     */
    public String getLabel(Object o) {
        //workspaces don't have a name
        return "Workspace";
    }
}
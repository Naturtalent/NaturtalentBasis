package it.naturtalent.e4.project.ui.ws;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;

/**
 * A WorkingSetFactory is used to recreate a persisted WorkingSet 
 * object.
 */
public class WorkingSetFactory implements IElementFactory {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IElementFactory
     */
    public IAdaptable createElement(IMemento memento) {
        String workingSetName = memento.getString(IWorkbenchConstants.TAG_NAME);
        String label = memento.getString(IWorkbenchConstants.TAG_LABEL);
        if (label == null) {
			label = workingSetName;
		}
        String workingSetEditPageId = memento
                .getString(IWorkbenchConstants.TAG_EDIT_PAGE_ID);
        String aggregateString = memento
				.getString(AbstractWorkingSet.TAG_AGGREGATE);
		boolean isAggregate = aggregateString != null
				&& Boolean.valueOf(aggregateString).booleanValue();

		if (workingSetName == null) {
			return null;
		}

        AbstractWorkingSet workingSet = null;
        
        if (isAggregate) {
			workingSet = new AggregateWorkingSet(workingSetName, label, memento);
		} else {
			workingSet = new WorkingSet(workingSetName, label, memento);
		}
        
        if (workingSetEditPageId != null) {
            workingSet.setId(workingSetEditPageId);
        } else if (!isAggregate) {
            // working sets created with builds 20020418 and 20020419 will not
            // have an edit page id. fix this automatically.
            workingSet.setId("org.eclipse.ui.resourceWorkingSetPage"); //$NON-NLS-1$
        }
        return workingSet;
    }
}

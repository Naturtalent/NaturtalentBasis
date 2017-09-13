package it.naturtalent.e4.project.ui.emf;

import it.naturtalent.emf.model.ModelEventKey;

public interface ProjectModelEventKey
{
	public static final String PROJECT_UNDO_MODELEVENT = ModelEventKey.PREFIX_MODELEVENT+"projectUndoModelEvent"; //$NON-NLS-N$
	public static final String PROJECT_VALIDATION_MODELEVENT = ModelEventKey.PREFIX_MODELEVENT+"projectValidationDetailsModelEvent"; //$NON-NLS-N$
	
	/*
	public static final String DEFAULT_NEW_MODELEVENT = PREFIX_MODELEVENT+"defaultNewModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_DELETE_MODELEVENT = PREFIX_MODELEVENT+"defaultDeleteModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_UNDO_MODELEVENT = PREFIX_MODELEVENT+"defaultUndoModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_SHOWDETAILS_MODELEVENT = PREFIX_MODELEVENT+"defaultShowDetailsModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_VALIDATION_MODELEVENT = PREFIX_MODELEVENT+"defaultValidationDetailsModelEvent"; //$NON-NLS-N$
	*/
}

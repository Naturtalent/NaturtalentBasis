package it.naturtalent.emf.model;

public interface ModelEventKey
{
	
	public static final String PREFIX_MODELEVENT = "prefixModelEvent/"; //$NON-NLS-N$
	
	public static final String DEFAULT_NEW_MODELEVENT = PREFIX_MODELEVENT+"defaultNewModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_DELETE_MODELEVENT = PREFIX_MODELEVENT+"defaultDeleteModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_UNDO_MODELEVENT = PREFIX_MODELEVENT+"defaultUndoModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_SHOWDETAILS_MODELEVENT = PREFIX_MODELEVENT+"defaultShowDetailsModelEvent"; //$NON-NLS-N$
	public static final String DEFAULT_VALIDATION_MODELEVENT = PREFIX_MODELEVENT+"defaultValidationDetailsModelEvent"; //$NON-NLS-N$
	
	/*
	public static final String DEFAULT_VIEWEVENT_ADDMASTER = MASTER_VIEWEVENT+"addMaster"; //$NON-NLS-N$
	public static final String DEFAULT_VIEWEVENT_DELETEMASTER = MASTER_VIEWEVENT+"deleteMaster"; //$NON-NLS-N$
	
	//public static final String MODEL_VALIDATIONEVENT = MASTER_VIEWEVENT+"modelValidationEvent"; //$NON-NLS-N$
	
	public static final String VIEWEVENT_UNDOMASTER = MASTER_VIEWEVENT+"undoMaster"; //$NON-NLS-N$
	public static final String VIEWEVENT_SHOWDETAILS = MASTER_VIEWEVENT+"showDetails"; //$NON-NLS-N$	
	
	// Validation Events
	public static final String MODEL_VALIDATIONEVENT = "modelValidationEvent"; //$NON-NLS-N$
	*/
}

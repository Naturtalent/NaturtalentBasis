package it.naturtalent.e4.project.expimp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.e4.project.expimp.messages"; //$NON-NLS-1$
	public static String SelectImportDialog_lblSource_text;
	public static String SelectImportDialog_btnBrowse_text;
	public static String SelectImportDialog_btnAllSelect_text;
	public static String SelectImportDialog_btnNoSelect_text;
	public static String SelectImportDialog_this_title;
	public static String SelectImportDialog_this_message;
	public static String SelectExportDialog_this_title;
	public static String SelectExportDialog_lblSource_text;
	public static String SelectExportDialog_this_message;
	public static String SelectExportDialog_btnAllSelect_text;
	public static String SelectExportDialog_btnNoSelect_text;
	public static String SelectExportDialog_btnBrowse_text;
	public static String SelectExportDialog_group_text;
	public static String SelectExportDialog_btnNewButton_text;
	public static String SelectExportDialog_btnWorkingsets_text;
	public static String SelectExportDialog_btnProjects_text;
	public static String SelectExportDialog_controlDecoration_descriptionText;
	public static String SelectExportDialog_DestDirectoryError;
	public static String SelectExportDialog_DestDirectoryErrorMessage;	
	
	public static String WizardDataTransfer_existsQuestion;
	public static String WizardDataTransfer_overwriteNameAndPathQuestion;
	public static String Question;
	public static String SelectImportDialog_group_text;
	public static String SelectImportDialog_btnWorkingSets_text;
	public static String SelectImportDialog_lblWorkingset_text;
	public static String SelectImportDialog_btnBrowseWorkingset_text;
	public static String SelectImportDialog_ColumnProjekte;
	public static String SelectImportDialog_ImportDirTitle;
	public static String SelectImportDialog_Message;
	public static String ImportExistProjects_this_title;
	public static String ImportExistProjects_this_message;
	public static String ImportExistProjects_lblExistingProjects_text;
	public static String ImportDialog_this_message;
	public static String ImportDialog_this_title;
	public static String ExportDialog_this_message;
	public static String ExportDialog_this_title;
	public static String ExportResources_Cancel;
	public static String ExportResources_Error;
	public static String ExportToDocument_MessageBlocked;
	public static String ExportToDocument_MessageTitleExport;
	public static String ExportToOODocument_SpreadSheetLabel;
	public static String SelectExportDialog_grpOptionen_text;
	public static String SelectExportDialog_btnRadioButton_text;
	public static String SelectExportDialog_btnRadioOO_text;
	public static String SelectExportDialog_btnRadioExcel_text;
	public static String SelectImportDialog_tblclmnProject_text;
	public static String SelectImportDialog_tblclmnNewColumn_text;	
	public static String SelectExportDialog_btnCheckButton_text;
	public static String SelectExportDialog_btnCheckButton_toolTipText;
	public static String AbstractImportDialog_this_message;
	public static String AbstractImportDialog_this_title;
	public static String AbstractImportDialog_lblSourcFile_text;
	public static String AbstractImportDialog_tblclmnName_text; 
	public static String AbstractImportDialog_btnSelect_text;
	public static String AbstractImportDialog_btnSelectAll_text;
	public static String AbstractImportDialog_btnNoSelection_text;
	public static String AbstractImportDialog_btnCheckOverwrite_text;
	public static String AbstractImportDialog_btnNewButton_text;
	public static String AbstractImportDialog_btnResetFilter_toolTipText;

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	////////////////////////////////////////////////////////////////////////////
	private Messages() {
		// do not instantiate
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Class initialization
	//
	////////////////////////////////////////////////////////////////////////////
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}

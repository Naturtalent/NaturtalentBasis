package it.naturtalent.e4.project.ui.datatransfer;

import org.eclipse.osgi.util.NLS;


public class DataTransferMessages extends NLS {
	private static final String BUNDLE_NAME = "it.naturtalent.e4.project.ui.datatransfer.messages";//$NON-NLS-1$

	// ==============================================================================
	// Data Transfer Wizards
	// ==============================================================================
	public static String DataTransfer_fileSystemTitle;
	public static String ZipExport_exportTitle;
	public static String ArchiveExport_exportTitle;

	public static String DataTransfer_browse;
	public static String DataTransfer_selectTypes;
	public static String DataTransfer_selectAll;
	public static String DataTransfer_deselectAll;
	public static String DataTransfer_refresh;
	public static String DataTransfer_cannotOverwrite;
	public static String DataTransfer_emptyString;
	public static String DataTransfer_scanningMatching;
	public static String DataTransfer_information;

	// --- Import Wizards ---
	public static String DataTransfer_importTitle;

	public static String DataTransfer_importTask;
	public static String ImportOperation_cannotCopy;
	public static String ImportOperation_importProblems;
	public static String ImportOperation_openStreamError;
	public static String ImportOperation_closeStreamError;
	public static String ImportOperation_coreImportError;
	public static String ImportOperation_targetSameAsSourceError;
	public static String ImportPage_filterSelections;

	public static String FileImport_selectSource;
	public static String FileImport_selectSourceTitle;
	public static String FileImport_fromDirectory;
	public static String FileImport_importFileSystem;
	public static String FileImport_overwriteExisting;
	public static String FileImport_createTopLevel;
	public static String FileImport_createVirtualFolders;
	public static String FileImport_importElementsAs;
	public static String FileImport_createVirtualFoldersTooltip;
	public static String FileImport_createLinksInWorkspace;
	public static String FileImport_advanced;
	public static String FileImport_noneSelected;
	public static String FileImport_cannotImportFilesUnderAVirtualFolder;
	public static String FileImport_haveToCreateLinksUnderAVirtualFolder;
	public static String FileImport_invalidSource;
	public static String FileImport_sourceEmpty;
	public static String FileImport_importProblems;
	public static String ZipImport_description;
	public static String ZipImport_couldNotClose;
	public static String ZipImport_badFormat;
	public static String ZipImport_couldNotRead;
	public static String ZipImport_fromFile;
	public static String ZipImportSource_title;

	public static String ArchiveImport_description;
	public static String ArchiveImport_fromFile;
	public static String ArchiveImportSource_title;
	public static String TarImport_badFormat;

	public static String WizardExternalProjectImportPage_locationError;
	public static String WizardExternalProjectImportPage_projectLocationEmpty;
	public static String WizardExternalProjectImportPage_projectExistsMessage;
	public static String WizardExternalProjectImportPage_projectContentsLabel;
	public static String WizardExternalProjectImportPage_nameLabel;
	public static String WizardExternalProjectImportPage_title;
	public static String WizardExternalProjectImportPage_description;
	public static String WizardExternalProjectImportPage_notAProject;
	public static String WizardProjectsImportPage_ProjectsListTitle;
	public static String WizardProjectsImportPage_ProcessingMessage;
	public static String WizardProjectsImportPage_SelectDialogTitle;
	public static String WizardProjectsImportPage_SearchingMessage;
	public static String WizardExternalProjectImportPage_errorMessage;
	public static String WizardProjectsImportPage_ImportProjectsTitle;
	public static String WizardExternalProjectImportPage_caseVariantExistsError;
	public static String WizardExternalProjectImportPage_directoryLabel;
	public static String WizardProjectsImportPage_RootSelectTitle;
	public static String WizardProjectsImportPage_ImportProjectsDescription;
	public static String WizardProjectsImportPage_CheckingMessage;
	public static String WizardProjectsImportPage_ArchiveSelectTitle;
	public static String WizardProjectsImportPage_SelectArchiveDialogTitle;
	public static String WizardProjectsImportPage_CreateProjectsTask;
	public static String WizardProjectsImportPage_CopyProjectsIntoWorkspace;
	public static String WizardProjectsImportPage_projectsInWorkspace;
	public static String WizardProjectsImportPage_noProjectsToImport;
	public static String WizardProjectsImportPage_projectLabel;

	// --- Export Wizards ---
	public static String DataTransfer_export;

	public static String DataTransfer_exportingTitle;
	public static String DataTransfer_createTargetDirectory;
	public static String DataTransfer_directoryCreationError;
	public static String DataTransfer_errorExporting;
	public static String DataTransfer_exportProblems;

	public static String ExportFile_overwriteExisting;
	public static String FileExport_selectDestinationTitle;
	public static String FileExport_selectDestinationMessage;
	public static String FileExport_exportLocalFileSystem;
	public static String FileExport_destinationEmpty;
	public static String FileExport_createDirectoryStructure;
	public static String FileExport_createSelectedDirectories;
	public static String FileExport_noneSelected;
	public static String FileExport_directoryExists;
	public static String FileExport_conflictingContainer;
	public static String FileExport_rootName;
	public static String FileSystemExportOperation_problemsExporting;
	public static String FileExport_toDirectory;
	public static String FileExport_damageWarning;

	public static String ZipExport_compressContents;
	public static String ZipExport_destinationLabel;
	public static String ZipExport_mustBeFile;
	public static String ZipExport_alreadyExists;
	public static String ZipExport_alreadyExistsError;
	public static String ZipExport_cannotOpen;
	public static String ZipExport_cannotClose;
	public static String ZipExport_selectDestinationTitle;
	public static String ZipExport_destinationEmpty;

	public static String ArchiveExport_description;
	public static String ArchiveExport_destinationLabel;
	public static String ArchiveExport_selectDestinationTitle;
	public static String ArchiveExport_destinationEmpty;
	public static String ArchiveExport_saveInZipFormat;
	public static String ArchiveExport_saveInTarFormat;

	public static String TarImport_invalid_tar_format;
	
	public static String ContainerGenerator_progressMessage;
	public static String ContainerGenerator_pathOccupied;
	
	public static String DropAdapter_title;
	public static String DragAdapter_title;
	public static String DragAdapter_checkDeleteMessage;		
	public static String DropAdapter_ok;
	public static String DropAdapter_problemImporting;
	public static String DropAdapter_problemsMoving;
	public static String DropAdapter_targetMustBeResource;
	public static String DropAdapter_canNotDropIntoClosedProject;
	public static String DropAdapter_resourcesCanNotBeSiblings;
	public static String DropAdapter_dropOperationErrorOther;
	public static String DropAdapter_overwriteQuery;
	public static String DropAdapter_question;
	
	public static String ReadOnlyCheck_problems;
	
	public static String CopyFilesAndFoldersOperation_infoNotFound;
	public static String CopyFilesAndFoldersOperation_deepMoveQuestion;
	public static String CopyFilesAndFoldersOperation_moveTitle;
	public static String CopyFilesAndFoldersOperation_interruptInfo;
	
	public static String MoveFilesAndFoldersOperation_operationTitle;
	public static String MoveFilesAndFoldersOperation_problemMessage;
	public static String MoveFilesAndFoldersOperation_moveFailedTitle;
	public static String MoveFilesAndFoldersOperation_sameSourceAndDest;
	public static String MoveResourceAction_title;
	public static String MoveResourceAction_checkMoveMessage;
	
	public static String AbstractResourcesOperation_MovingResources;
	public static String AbstractWorkspaceOperation_ExecuteErrorTitle;
	public static String AbstractWorkspaceOperation_RedoErrorTitle;
	public static String AbstractWorkspaceOperation_UndoErrorTitle;
	public static String AbstractWorkspaceOperation_SideEffectsWarningTitle;
	public static String AbstractWorkspaceOperation_ExecuteSideEffectsWarningMessage;
	public static String AbstractWorkspaceOperation_ErrorInvalidMessage;
	public static String AbstractWorkspaceOperation_GenericWarningMessage;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DataTransferMessages.class);
	}
}

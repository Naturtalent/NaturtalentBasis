package it.naturtalent.e4.project.listeners;

import org.osgi.service.event.EventConstants;

public class WorkspaceEventConstants
{
	public static final String WORKSPACEMODIFY = EventConstants.class.getName().replace('.', '/') + "/WORKSPACEMODIFY";
	public static final String WORKSPACEMODIFY_NEW_RESOURCES = "newresources";
	public static final String WORKSPACEMODIFY_DELETE_RESOURCES = "deleteresources";
	public static final String WORKSPACEMODIFY_RENAME_RESOURCES = "renameresources";
}

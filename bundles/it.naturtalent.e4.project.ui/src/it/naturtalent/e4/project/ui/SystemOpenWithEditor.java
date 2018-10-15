package it.naturtalent.e4.project.ui;

import it.naturtalent.application.services.IOpenWithEditorAdapter;

public class SystemOpenWithEditor implements IOpenWithEditorAdapter
{

	@Override
	public String getCommandID()
	{		
		return "it.naturtalent.e4.project.ui.command.openwithsystemeditor";
	}

	@Override
	public String getMenuID()
	{		
		return "it.naturtalent.e4.project.menu.systemopen";
	}

	@Override
	public String getMenuLabel()
	{		
		return "System Editor";
	}

	@Override
	public String getContribURI()
	{
		return null;
	}

	@Override
	public boolean getType()
	{		
		return true;
	}

	@Override
	public int getIndex()
	{
		return 0;
	}

	@Override
	public boolean isExecutable(String filePath)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(String filePath)
	{
		// TODO Auto-generated method stub
	}

}

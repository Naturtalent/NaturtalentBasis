package it.naturtalent.e4.project.ui;

import it.naturtalent.application.services.IOpenWithEditorAdapter;

public class TestOpenWithAdapter implements IOpenWithEditorAdapter
{

	@Override
	public String getCommandID()
	{		
		return "it.naturtalent.e4.project.ui.command.testOpenWith";
	}

	@Override
	public String getMenuID()
	{		
		return "it.naturtalent.e4.project.menu.testopen";		
	}

	@Override
	public String getMenuLabel()
	{		
		return "Test";
	}

	
	@Override
	public String getContribURI()
	{
		return null;
		//return "bundleclass://it.naturtalent.e4.project.ui/it.naturtalent.e4.project.ui.handlers.TESThandler";		
	}

	@Override
	public boolean getType()
	{		
		return true;
	}

	@Override
	public int getIndex()
	{		
		return 1;
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

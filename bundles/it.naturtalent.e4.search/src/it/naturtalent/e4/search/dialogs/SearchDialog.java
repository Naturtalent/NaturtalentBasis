package it.naturtalent.e4.search.dialogs;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.search.IProjectSearchPageRegistry;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import org.eclipse.swt.widgets.Label;
//import it.naturtalent.e4.search.Activator;

/**
 * Suchdialog steuert auf der Grundlage des SearchPageRegistry die entsprechenden Suchfunktionen. 
 * Jede SearchPage wird einem TabFolder zugeordnet und kann hierueber ausgewaehlt werden.
 * 
 * @author apel.dieter
 * 
 */
public class SearchDialog extends TitleAreaDialog
{

	// zentrale Registry mit den Searchpages
	private IProjectSearchPageRegistry searchPageregistry;

	private int PREFERRED_CONTENT_HEIGHT = 450;

	private int PREFERRED_CONTENT_WIDTH = 750;

	// Seite, die fuer die momentane Suche verwendet wird
	private ISearchInEclipsePage page;

	private TabFolder tabSearchFolder;


	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	
	public SearchDialog()
	{
		super(Display.getDefault().getActiveShell());		
	}
	
	public SearchDialog(Shell parentShell)
	{
		super(parentShell);		
	}
	
	@PostConstruct
	public void postConstruct(@Optional IProjectSearchPageRegistry searchPageregistry)
	{
		this.searchPageregistry = searchPageregistry;		
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData containerLayoutData = new GridData(GridData.FILL_BOTH);
		containerLayoutData.heightHint = PREFERRED_CONTENT_HEIGHT;
		containerLayoutData.widthHint = PREFERRED_CONTENT_WIDTH;
		container.setLayoutData(containerLayoutData);

		tabSearchFolder = new TabFolder(container, SWT.NONE);
		GridData gd_tabSearchFolder = new GridData(GridData.FILL_BOTH);
		gd_tabSearchFolder.grabExcessVerticalSpace = false;
		gd_tabSearchFolder.heightHint = 441;
		tabSearchFolder.setLayoutData(gd_tabSearchFolder);

		// die SearchPages den TabFoldern zuordnen
		if (searchPageregistry != null)
		{
			ISearchInEclipsePage[] pages = searchPageregistry.getSearchPages();
			
			for (int i = 0; i < pages.length; i++)
			{
				ISearchInEclipsePage page = pages[i]; 
						
				TabItem tabItem = new TabItem(tabSearchFolder, SWT.NONE);
				tabItem.setText(page.getLabel());

				Control partControl = page.createControl(tabSearchFolder);
				tabItem.setControl(partControl);
			}
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(997, 623);
	}


	@Override
	protected void okPressed()
	{
		page = getSelectedPage();
		page.performSearch(null);		
		super.okPressed();
	}
	
	public ISearchInEclipsePage getSearchPage()
	{
		return page;
	}

	/*
	 * die Suche der ausgewaelten Seite starten
	 */
	public void startSearch()
	{
		page = getSelectedPage();
		page.performSearch(null);		
	}

	// die selektierte SearchPage abfragen
	private ISearchInEclipsePage getSelectedPage()
	{
		int index = tabSearchFolder.getSelectionIndex();
		if (index == -1)
		{
			// ???
			return searchPageregistry.getSearchPages()[0];
		}
		return searchPageregistry.getSearchPages()[index];
	}

}

package it.naturtalent.e4.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import it.naturtalent.e4.search.DiagnoseSearchPage.DiagnoseCheckEnum;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * UI der Foldersuchseite.
 * 
 * Combo zur Eingabe eines Suchmusters fuer den Verzeichnisnamen.
 * Eine Gruppe in der die Auswahl der Projekte eingeschraenkt werden kann. 
 * 
 * @author dieter
 *
 */
public class DiagnoseSearchComposite extends Composite
{
	
	private List <Button>radioButtons = new ArrayList<Button>(); 
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DiagnoseSearchComposite(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(1, false));
				
		Label lblPlatzhalter = new Label(composite, SWT.NONE);
		
		Group groupDiag = new Group(composite, SWT.NONE);
		groupDiag.setText("Prüffälle");
		groupDiag.setLayout(new FillLayout(SWT.VERTICAL));
		groupDiag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblPlaceholder = new Label(groupDiag, SWT.NONE);

		Button btnRadioNoReal = new Button(groupDiag, SWT.RADIO);
		btnRadioNoReal.setToolTipText("sucht Namen die keine Projektentsprechung haben");
		btnRadioNoReal.setText("fehlende Projekte");
		btnRadioNoReal.setData(DiagnoseCheckEnum.NOREALPROJECT);
		radioButtons.add(btnRadioNoReal);

		Button btnRadioNoName = new Button(groupDiag, SWT.RADIO);
		btnRadioNoName.setSelection(true);
		btnRadioNoName.setToolTipText("sucht Projekte mit fehlenden Namen");
		btnRadioNoName.setText("fehlende Projektnamen");
		btnRadioNoName.setData(DiagnoseCheckEnum.NOPROJECTNAME);
		radioButtons.add(btnRadioNoName);
		
		Button btnRadioNoFileDir = new Button(groupDiag, SWT.RADIO);
		btnRadioNoFileDir.setToolTipText("sucht Projekte mit fehlendem Verzeichnis");
		btnRadioNoFileDir.setText("fehlendes Projektverzeichnis");
		btnRadioNoFileDir.setData(DiagnoseCheckEnum.NOPROJECTDIR);
		radioButtons.add(btnRadioNoFileDir);
		
		Button btnRadioNoQualified = new Button(groupDiag, SWT.RADIO);
		btnRadioNoQualified.setToolTipText("sucht Projekte mit fehlenden qualifizierten Namen");
		btnRadioNoQualified.setText("fehlende qualifizierte Namen");
		btnRadioNoQualified.setData(DiagnoseCheckEnum.NOQUALIFIEDPROJECTNAME);
		radioButtons.add(btnRadioNoQualified);
		
		Label lblPlatzhalter2 = new Label(groupDiag, SWT.NONE);
		
	}
	
	/*
	 * gibt den selektierten Check zurueck
	 */
	public DiagnoseCheckEnum getSelectedCheck()
	{
		for(Button radioButton : radioButtons)
		{
			if(radioButton.getSelection())
				return (DiagnoseCheckEnum) radioButton.getData();
		}
		return null;
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}

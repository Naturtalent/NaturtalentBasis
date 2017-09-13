package it.naturtalent.e4.project.ui.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class OrdnerArchivComposite extends Composite
{
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtArchivOrt;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OrdnerArchivComposite(Composite parent, int style)
	{
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(1, false));
		
		Section sctnOrdnerArchiv = formToolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		GridData gd_sctnOrdnerArchiv = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_sctnOrdnerArchiv.widthHint = 269;
		sctnOrdnerArchiv.setLayoutData(gd_sctnOrdnerArchiv);
		formToolkit.paintBordersFor(sctnOrdnerArchiv);
		sctnOrdnerArchiv.setText("Papier Archiv");
		
		Composite composite = new Composite(sctnOrdnerArchiv, SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		sctnOrdnerArchiv.setClient(composite);
		composite.setLayout(new GridLayout(4, false));
		
		ImageHyperlink mghprlnkOrdner = formToolkit.createImageHyperlink(composite, SWT.NONE);
		mghprlnkOrdner.setBounds(0, 0, 142, 21);
		formToolkit.paintBordersFor(mghprlnkOrdner);
		mghprlnkOrdner.setText("Ordner");
		
		Spinner spinnerOrdner = new Spinner(composite, SWT.BORDER);
		formToolkit.adapt(spinnerOrdner);
		formToolkit.paintBordersFor(spinnerOrdner);
		
		Label lblRegister = new Label(composite, SWT.NONE);
		formToolkit.adapt(lblRegister, true, true);
		lblRegister.setText("Register");
		
		Spinner spinner = new Spinner(composite, SWT.BORDER);
		formToolkit.adapt(spinner);
		formToolkit.paintBordersFor(spinner);
		
		Label lblArchivOrt = formToolkit.createLabel(composite, "Ort", SWT.NONE);
		lblArchivOrt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtArchivOrt = formToolkit.createText(composite, "New Text", SWT.NONE);
		txtArchivOrt.setText("");
		txtArchivOrt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}

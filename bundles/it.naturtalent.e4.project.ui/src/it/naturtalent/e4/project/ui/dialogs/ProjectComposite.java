package it.naturtalent.e4.project.ui.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;

public class ProjectComposite extends Composite
{
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text text;
	private Text txtNotizen;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectComposite(Composite parent, int style)
	{
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(2, false));
		
		Label lblProjectName = new Label(this, SWT.NONE);
		lblProjectName.setBounds(0, 0, 70, 17);
		formToolkit.adapt(lblProjectName, true, true);
		lblProjectName.setText("Projektname");
		
		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text.setBounds(0, 0, 75, 27);
		formToolkit.adapt(text, true, true);
		
		new Label(this, SWT.NONE);		
		new Label(this, SWT.NONE);
		
		Section sctnProject = formToolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);		
		sctnProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		formToolkit.paintBordersFor(sctnProject);
		sctnProject.setText("Projekt");
		sctnProject.setExpanded(true);
		
		Composite composite = new Composite(sctnProject, SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		sctnProject.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblNotizen = formToolkit.createLabel(composite, "Notizen", SWT.NONE);
		lblNotizen.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtNotizen = formToolkit.createText(composite, "New Text", SWT.MULTI);
		txtNotizen.setText("");
		GridData gd_txtNotizen = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtNotizen.heightHint = 93;
		txtNotizen.setLayoutData(gd_txtNotizen);
		
		Label lblNewLabel = formToolkit.createLabel(composite, "", SWT.NONE);
		new Label(composite, SWT.NONE);
				
		Group groupWorkingset = new Group(composite, SWT.NONE);
		groupWorkingset.setText("Working sets");
		groupWorkingset.setLayout(new GridLayout(3, false));
		groupWorkingset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		formToolkit.adapt(groupWorkingset);
		formToolkit.paintBordersFor(groupWorkingset);
		
		Button btnCheckWorkingSet = new Button(groupWorkingset, SWT.CHECK);
		btnCheckWorkingSet.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		formToolkit.adapt(btnCheckWorkingSet, true, true);
		btnCheckWorkingSet.setText("Projekt einem Working set zuordnen");
		
		Label lblWorkingSets = formToolkit.createLabel(groupWorkingset, "Working sets:", SWT.NONE);
		
		CCombo combo = new CCombo(groupWorkingset, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 274;
		combo.setLayoutData(gd_combo);
		formToolkit.adapt(combo);
		formToolkit.paintBordersFor(combo);
		
		Button btnSelect = formToolkit.createButton(groupWorkingset, "select", SWT.NONE);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}

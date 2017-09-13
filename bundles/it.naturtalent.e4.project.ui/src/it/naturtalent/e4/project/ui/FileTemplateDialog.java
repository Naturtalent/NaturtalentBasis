package it.naturtalent.e4.project.ui;



import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileTemplateDialog extends Dialog
{
	private DataBindingContext m_bindingContext;

	private MultiStatus mStatus;

	/**
	 * Interne Klasse zum ueberpruefen des Textfeldes 
	 * 
	 * @author dieter
	 * 
	 */
	public static class URLValidator implements IValidator
	{
		public URLValidator()
		{
			super();
		}

		@Override
		public IStatus validate(Object value)
		{
			controlDecorationURL.setDescriptionText("");
			controlDecorationURL.hide();
			
			if (StringUtils.isNotEmpty((String) value))
			{
				try
				{
					URL url = new URL((String) value);
					controlDecorationURL.hide();
					if(btnOk != null)
						btnOk.setEnabled(!controlDecorationName.isVisible());
					return Status.OK_STATUS;
					
				} catch (MalformedURLException e)
				{
				}
				
				controlDecorationURL.setDescriptionText("ungültige URL");
				if(btnOk != null)
					btnOk.setEnabled(false);							
			}
			else
			{				
				controlDecorationURL.setDescriptionText("leeres Textfeld");				
				if(btnOk != null)
					btnOk.setEnabled(false);
			}
			
			controlDecorationURL.show();
			return ValidationStatus.error("URL unvalid");
		}
	}

	/**
	 * Interne Klasse zum ueberpruefen des Textfeldes 
	 * 
	 * @author dieter
	 * 
	 */
	public static class NameValidator implements IValidator
	{
		public NameValidator()
		{
			super();
		}

		@Override
		public IStatus validate(Object value)
		{
			controlDecorationName.setDescriptionText("");

			if (StringUtils.isNotEmpty((String) value))
			{				
				// Name OK
				controlDecorationName.hide();
				if (btnOk != null)
					btnOk.setEnabled(!controlDecorationURL.isVisible());
				return Status.OK_STATUS;
			}
			else
			{
				// leeres Feld
				controlDecorationName.show();
				controlDecorationName.setDescriptionText("leeres Textfeld");

				if (btnOk != null)
					btnOk.setEnabled(false);

				return ValidationStatus.error("Empty Addressname");
			}
		}
	}
	
	
	public class PreferenceData
	{
		String name;
		String url;
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public String getUrl()
		{
			return url;
		}
		public void setUrl(String url)
		{
			this.url = url;
		}
	}
	private PreferenceData preferenceData = new PreferenceData();

	private Text textName;
	private Text textURL;
	private static ControlDecoration controlDecorationURL;
	private static ControlDecoration controlDecorationName;
	private static Button btnOk;
	
	//private static Map<String,String>templateMap;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public FileTemplateDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(4, false));
		
		Label lblDialog = new Label(container, SWT.NONE);
		lblDialog.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		lblDialog.setText(Messages.FileTemplateDialog_lblDialog_text);
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText(Messages.FileTemplateDialog_lblName_text);
		
		textName = new Text(container, SWT.BORDER);
		GridData gd_textName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textName.widthHint = 244;
		textName.setLayoutData(gd_textName);
		
		controlDecorationName = new ControlDecoration(textName, SWT.LEFT | SWT.TOP);
		//controlDecorationName.setImage(SWTResourceManager.getImage(FileTemplateDialog.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif"));
		controlDecorationName.setImage(Icon.OVERLAY_ERROR.getImage(IconSize._7x8_OverlayIconSize));		
		controlDecorationName.setDescriptionText("leeres Textfeld");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText(Messages.FileTemplateDialog_lblNewLabel_text);
		
		textURL = new Text(container, SWT.BORDER);
		textURL.setEditable(false);
		textURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		controlDecorationURL = new ControlDecoration(textURL, SWT.LEFT | SWT.TOP);
		controlDecorationURL.setImage(Icon.OVERLAY_ERROR.getImage(IconSize._7x8_OverlayIconSize));
		controlDecorationURL.setDescriptionText("Someone description");
		
		Button btnSelect = new Button(container, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
				String fn = dlg.open();
				if (fn != null)
				{					
					try
					{
						URL url = new File(fn).toURI().toURL();
						textURL.setText(url.toString());
						
					} catch (MalformedURLException e1)
					{
					}
				}	
			}
		});
		btnSelect.setText("auswählen");
		new Label(container, SWT.NONE);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		btnOk = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		btnOk.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(663, 189);
	}

	
	public PreferenceData createPreferenceData()
	{
		return new PreferenceData();
	}

	public void setPreferenceData(PreferenceData preferenceData)
	{
			if (m_bindingContext != null)
				m_bindingContext.dispose();			
			this.preferenceData = preferenceData;
			m_bindingContext = initDataBindings();
	}
	
	public PreferenceData getPreferenceData()
	{
		return preferenceData;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(textName);
		IObservableValue namePreferenceDataObserveValue = PojoProperties.value("name").observe(preferenceData);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new NameValidator());
		bindingContext.bindValue(observeTextTextNameObserveWidget, namePreferenceDataObserveValue, strategy, null);
		//
		IObservableValue observeTextTextURLObserveWidget = WidgetProperties.text(SWT.Modify).observe(textURL);
		IObservableValue urlPreferenceDataObserveValue = PojoProperties.value("url").observe(preferenceData);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setAfterGetValidator(new URLValidator());
		bindingContext.bindValue(observeTextTextURLObserveWidget, urlPreferenceDataObserveValue, strategy_1, null);
		//
		return bindingContext;
	}
}

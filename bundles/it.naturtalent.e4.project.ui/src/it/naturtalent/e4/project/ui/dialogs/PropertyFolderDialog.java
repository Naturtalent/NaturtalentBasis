package it.naturtalent.e4.project.ui.dialogs;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.wb.swt.ResourceManager;

public class PropertyFolderDialog extends TitleAreaDialog
{
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtPath;
	
	private IFolder selectedFolder;
	
	// der benutzte Zwischenspeicher
	private Clipboard clipboard;


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public PropertyFolderDialog(Shell parentShell)
	{
		super(parentShell);
		
		// Clipboard aktivieren
		clipboard = new Clipboard(Display.getDefault());
	}
	
	@PostConstruct
	public void postConstruct(@Optional EPartService ePartService)
	{
		MPart mPart = ePartService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		IResourceNavigator resourceNavigator = (IResourceNavigator) mPart.getObject();
		TreeViewer treeViewer = resourceNavigator.getViewer();
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();	
		Object selObj = selection.getFirstElement();
		if (selObj instanceof IFolder)
			selectedFolder = (IFolder) selObj;
	}
	
	@Override
	public boolean close()
	{
		if (clipboard != null)
		{
			clipboard.dispose();
			clipboard = null;
		}

		return super.close();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitle("Verzeichniseigenschaften");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Hyperlink hprlnkPath = formToolkit.createHyperlink(container, "Pfad", SWT.NONE);
		hprlnkPath.addHyperlinkListener(new HyperlinkAdapter()
		{
		public void linkActivated(HyperlinkEvent e)
			{
				try
				{					
					if (SystemUtils.IS_OS_LINUX)
						Runtime.getRuntime().exec("nautilus " + txtPath.getText());
					else
						Runtime.getRuntime().exec("explorer " + '"'+selectedFolder.getLocation().toOSString()+'"');

				} catch (Exception exp)
				{
					exp.printStackTrace();
				}
			}
		});
		hprlnkPath.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		formToolkit.paintBordersFor(hprlnkPath);
		
		txtPath = formToolkit.createText(container, "New Text", SWT.BORDER);
		txtPath.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPath.setText(selectedFolder.getLocation().toOSString());
		
		ImageHyperlink mghprlnkClipboard = formToolkit.createImageHyperlink(container, SWT.NONE);
		mghprlnkClipboard.setToolTipText("Pfad in Zwischenablage kopieren");
		mghprlnkClipboard.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		mghprlnkClipboard.setImage(ResourceManager.getPluginImage("it.naturtalent.e4.project.ui", "icons/copy_edit.gif"));
		formToolkit.paintBordersFor(mghprlnkClipboard);
		mghprlnkClipboard.setText("");
		mghprlnkClipboard.addHyperlinkListener(new HyperlinkAdapter()
		{
		public void linkActivated(HyperlinkEvent e)
			{
			clipboard.setContents(new Object[]
					{selectedFolder.getLocation().toOSString()}, new Transfer[]
							{ TextTransfer.getInstance() });
			}
		});
		return area;
	}

	/**
	 * Create contents of the button bar.
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
		return new Point(450, 300);
	}
	


}

package it.naturtalent.e4.project.expimp.dialogs;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class RestoreNtProjektDialog extends TitleAreaDialog
{
	
	public static final int EOF = (-1);
	
	private String importFile;
	
	private Combo comboQuellverzeichnis;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RestoreNtProjektDialog(Shell parentShell)
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
		setTitle("Backup-Projekte wieder herstellen");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblRestoreDir = new Label(container, SWT.NONE);
		lblRestoreDir.setText("gezippte Datei mit den Backupdaten");
		new Label(container, SWT.NONE);
		
		comboQuellverzeichnis = new Combo(container, SWT.NONE);
		comboQuellverzeichnis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(parent.getShell());
				
				fileDialog.setText("Importverzeichnis");
				fileDialog.setFilterExtensions(new String[]{"*.zip"}); //$NON-NLS-1$
				//fileDialog.setFilterPath(importPath);
								
				importFile = fileDialog.open();
				if (importFile != null)
					comboQuellverzeichnis.setText(importFile);				
			}
		});
		btnBrowse.setText("browse");

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
		return new Point(666, 300);
	}
	
	/*
	 * das Archiv 'srcZipFile' im Zielverzeichnis entpacken
	 */
	public static void unzipArchiv(String srcZipFile, File targetDir)
	{
		try
		{
			ZipFile zipFile = new ZipFile(srcZipFile);

			System.out.println("Program Start unzipping the given zipfile");
			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();)
			{
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				//System.out.print(zipEntry.getName() + " .");
				saveEntry(zipFile, zipEntry, targetDir);
				//System.out.println(". unpacked");
			}
			System.out.println("zipfile wurde erfolgreich entpackt");
			
		} catch (FileNotFoundException e)
		{
			System.out.println("zipfile not found");
		} catch (ZipException e)
		{
			System.out.println("zip error...");
		} catch (IOException e)
		{
			System.out.println("IO error...");
		}

	}
	
	/*
	 * ein einzelner Eintrag im Zielverzeichnis speichern
	 */
	public static void saveEntry(ZipFile zipFile, ZipEntry zipEntry, File targetDir) throws ZipException, IOException
	{
		File file = new File(targetDir, zipEntry.getName());

		if (zipEntry.isDirectory())
			file.mkdirs();
		
		else
		{
			InputStream is = zipFile.getInputStream(zipEntry);
			BufferedInputStream bis = new BufferedInputStream(is);
			File dir = new File(file.getParent());
			dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			for (int c; (c = bis.read()) != EOF;) // oder schneller
				bos.write((byte) c);

			bos.close();
			fos.close();
		}
	}

	public String getImportFile()
	{
		return importFile;
	}
	


}

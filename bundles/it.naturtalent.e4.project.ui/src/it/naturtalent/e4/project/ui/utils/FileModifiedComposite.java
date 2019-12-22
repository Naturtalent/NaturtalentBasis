package it.naturtalent.e4.project.ui.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Mit diesem Composite koennen Modifikationen im Dateisystem (Dateien und Verzeichnisse) eingeblendet werden.
 * 
 * @author dieter
 *
 */
public class FileModifiedComposite extends Composite
{
	private Text textCreated;
	private Text textModified;
	private Text textPath;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FileModifiedComposite(Composite parent, int style)
	{
		super(parent, style);	
		setLayout(new GridLayout(4, false));
		new Label(this, SWT.NONE);
		
		Label lblPath = new Label(this, SWT.NONE);
		lblPath.setText("Pfad:");
		new Label(this, SWT.NONE);
		
		textPath = new Text(this, SWT.BORDER);
		textPath.setEditable(false);
		textPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		
		Label lblcreatedDate = new Label(this, SWT.NONE);
		lblcreatedDate.setText("erstellt:");
		new Label(this, SWT.NONE);
		
		textCreated = new Text(this, SWT.BORDER);
		textCreated.setEditable(false);
		textCreated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		
		Label lblModify = new Label(this, SWT.NONE);
		lblModify.setText("zuletzt modifiziert:");
		new Label(this, SWT.NONE);
		
		textModified = new Text(this, SWT.BORDER);
		textModified.setEditable(false);
		textModified.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void setResource(IResource resource)
	{
		long modified;		
		
		File file =  new File(resource.getLocation().toOSString());
		textPath.setText(file.getPath());
		
		Calendar modCal = Calendar.getInstance();
		SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
		if(resource.getType() == IResource.FILE)		
			modified = file.lastModified();
		else
			modified = getLatestModifiedDate(file);		
		modCal.setTimeInMillis(modified);			
		textModified.setText(format1.format(modCal.getTime()));
		
		try
		{			
			if(resource.getType() == IResource.PROJECT)
			{
				// Erstellungsdatum des Projekts aus der ID extrahieren
				IProject iProject = (IProject) resource; 
				String stgDate = iProject.getName().substring(0, iProject.getName().indexOf('-'));			
				modified =NumberUtils.createLong(stgDate).longValue();
				modCal.setTimeInMillis(modified);
				textCreated.setText(format1.format(modCal.getTime()));
			}
			else
			{
				// Das Erstellungsdatum ist in Linux identisch mit lastModify
				BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				modified = attributes.creationTime().to(TimeUnit.MILLISECONDS);
				modCal.setTimeInMillis(modified);
				textCreated.setText(format1.format(modCal.getTime()));
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// letzte Aenderung in einer Resource (Project/Folder) ermitteln
	public static long getLatestModifiedDate(File dir)
	{
		File[] files = dir.listFiles();
		long latestDate = 0;
		for (File file : files)
		{
			long fileModifiedDate = file.isDirectory()
					? getLatestModifiedDate(file)
					: file.lastModified();
			if (fileModifiedDate > latestDate)
			{
				latestDate = fileModifiedDate;
			}
		}
		return Math.max(latestDate, dir.lastModified());
	}


}

package it.naturtalent.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


public class ChooseWorkspaceData
{	


	//private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$
	//private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$
	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$
	//private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$
	//private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$
	private static final String CMD_NAME = "-name"; //$NON-NLS-1$
	private static final String CMD_DATA = "-data"; //$NON-NLS-1$
	private static final String CMD_LAUNCHER = "-launcher"; //$NON-NLS-1$
	//private static final String CMD_NL = "-nl"; //$NON-NLS-1$
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$
	private static final String COMMAND_PREFIX = "-"; //$NON-NLS-1$

	private static final String DJAVA_LIBRARY_PATH = "-Djava.library.path"; //$NON-NLS-1$
	
	private File iniFile;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	// relevante Commands im Laucher-IniFile	
	private enum INI_CMD
	{
		DATA,
		CLEARPERSITEDSTATE,
		VMARGS,
		DJAVA_LIBRARY_PATH,
		VM,
	}
	
	// Map mit den definierten Commands
	private Map<INI_CMD,String>inimap = new HashMap<INI_CMD,String>();
	{
		inimap.put(INI_CMD.DATA, "-data");  //$NON-NLS-N$
		inimap.put(INI_CMD.VMARGS, "-vmargs"); //$NON-NLS-N$
		inimap.put(INI_CMD.CLEARPERSITEDSTATE, "-clearPersistedState"); //$NON-NLS-N$
		inimap.put(INI_CMD.DJAVA_LIBRARY_PATH, "-Djava.library.path"); //$NON-NLS-N$
		inimap.put(INI_CMD.VM, "-vm"); //$NON-NLS-N$
	}
	
	// Rueckgabe des Commands aus dem Map
	private String getINICommand(INI_CMD command)
	{
		return inimap.get(command);
	}

	/**
	 * CMD_DATA Workspacepfad eintragen
	 * 
	 * Eine Konfigurationsdatei lesen, parsen und wieder schreiben.
	 * Der Workspacepfad wird durch den uebergebenen ersetzt.
	 * 
	 * @param workspacePath
	 * @throws Exception
	 */
	public void setCommandData(String workspacePath) throws Exception
	{	
		// die aktuelle Datei lesen und parsen
		String iniFileContent = getLauncherIniFileContent();
		Map<String, String>parseMap = parseIniFile(iniFileContent);
				
		parseMap.put(CMD_DATA, workspacePath);
		validateIniContent(parseMap);
		
		// Datei mit aktuellen Inhalt wieder schreiben
		String content = createIniContent(parseMap);
		FileUtils.writeStringToFile(iniFile, content);	
	}
	
	/*
	 * Den Inhalt der ini-Datei in key/value zerlegen und in einer Map zurueckgeben.
	 * 
	 */
	private Map<String, String>	parseIniFile(String iniFileContent)
	{
		String key, value;
		Map<String, String>parseMap = new LinkedHashMap<String, String>();
		
		// Abbruch, wenn keine ini-Datei gelesen werden konnte (z.B. Aufruf innerhalb der IDE)
		String phase1[] = StringUtils.split(iniFileContent, NEW_LINE);
		if(!ArrayUtils.isEmpty(phase1))
		{
			for (int i = 0; i < phase1.length; i++)
			{
				if (StringUtils.startsWith(phase1[i], "-"))
				{
					key = phase1[i];
					if (i + 1 >= phase1.length)
					{
						// Stop, wenn key = letzte Zeile
						parseMap.put(key, "");
						break;
					}

					value = phase1[i + 1];
					if (!StringUtils.startsWith(value, "-"))
					{
						// klassisches key/value Parsing
						parseMap.put(key, value);
						i++;
					}
					else
					{
						// key ohne value
						parseMap.put(key, "");
					}
				}
			}
		}

		return parseMap;
	}
	
	private Map<String, String> validateIniContent(Map<String, String>parseMap)
	{
		// entfernt CMD_DATA mit Pfadangabe in einer Zeile
		parseMap = removeInlineDataKey(parseMap);
		
		// verschiebt JavaLibraryPath ans Ende der Datei
		parseMap = moveJavaLibraryToEnd(parseMap);
		
		return parseMap;
	}

	/*
	 * verschiebt JavaLibraryPath ans Ende der Datei
	 */
	private Map<String, String> moveJavaLibraryToEnd(Map<String, String>parseMap)
	{
		List<String> removeKeys = new ArrayList<String>();
		String javaLibraryPath = null;
		
		for (String key : parseMap.keySet())
		{			
			if (StringUtils.equals(key, CMD_VMARGS))					
				removeKeys.add(key);
			
			if (StringUtils.startsWith(key, DJAVA_LIBRARY_PATH))
			{
				javaLibraryPath = key;
				removeKeys.add(key);
			}
		}
		
		for(String key : removeKeys)
			parseMap.remove(key);
		
		if(javaLibraryPath != null)
		{			
			parseMap.put(CMD_VMARGS, "");
			parseMap.put(javaLibraryPath, "");
		}
			
		return parseMap;
	}

	/**
	 * DJAVA_LIBRARY_PATH LibPath eintragen
	 * 
	 * Eine Konfigurationsdatei lesen, parsen und wieder schreiben.
	 * Der LibPath wird durch den uebergebenen ersetzt.
	 * 
	 * @param libPath
	 */
	public void setJavaLibraryPath(String libPath)
	{
		StringBuilder buildJavaLibPath = null;
		String vmargs = CMD_VMARGS;
		try
		{
			// die aktuelle Datei lesen und parsen
			String iniFileContent = getLauncherIniFileContent();
			Map<String, String>parseMap = parseIniFile(iniFileContent);
			
			// Abbruch, wenn keine Datei gelesen werden konnte
			if(parseMap.isEmpty())
				return;
			
			validateIniContent(parseMap);

			// DJAVA_LIBRARY_PATH Command zusammensetzen
			buildJavaLibPath = new StringBuilder(DJAVA_LIBRARY_PATH);
			buildJavaLibPath.append("=");
			buildJavaLibPath.append(libPath);

			// einen 
			for (String key : parseMap.keySet())
			{			
				if (StringUtils.startsWith(key, DJAVA_LIBRARY_PATH))
				{
					// bestehenden Eintrag loeschen, vmargs = null markiert Eintrag als vorhanden
					parseMap.remove(key);
					vmargs = null;
				}
			}
			
			if(vmargs != null)
				parseMap.put(CMD_VMARGS, "");
				
			parseMap.put(buildJavaLibPath.toString(), "");
			
			// Datei mit aktuellen Inhalt wieder schreiben
			String content = createIniContent(parseMap);
			FileUtils.writeStringToFile(iniFile, content);
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Rueckgabe des LauncherIniFiles
	 * 
	 * @param commandLine
	 * @return
	 */
	
	public File getLauncherIniFile()
	{
		// fuer Debug in der IDE ini.-File im Launcherpfad direkt adressieren
		//return new File("/home/dieter/NaturtalentTest/RCP product-linux.gtk.x86","NaturTalent.ini");
		
		String commandLine = System.getProperty(PROP_COMMANDS);
		return getLauncherIniFile(commandLine);
		//return getLauncherIniFileTEST();
	}

	/*
	 * nur zum Test
	 */
	public File getLauncherIniFileTEST()
	{
		return new File("/home/dieter/NaturtalentTest/RCP product-linux.gtk.x86_64","NaturTalent.ini");
		//return new File("/home/dieter/NaturtalentTest/RCP product-linux.gtk.x86_64","NaturTalentOxygen.ini");
		
	}

	/**
	 * Rueckgabe des LauncherIniFiles
	 * 
	 * @param commandLine
	 * @return
	 */
	public File getLauncherIniFile(String commandLine)
	{
		String baseName;
		File launcherIniFile = null;				
		
		String property = getProperty(commandLine, CMD_LAUNCHER);		
		if(StringUtils.isNotEmpty(property))
		{
			baseName = FilenameUtils.getBaseName(property)+".ini";			
			File launcherFile = new File(property);			
			if(launcherFile.isDirectory())
				launcherIniFile = new File(launcherFile,baseName);
			else
			{				
				String path = launcherFile.getParent();				
				launcherIniFile = new File(path,baseName);
			}
	
			if(!launcherIniFile.exists())
				return null;
		}
		
		return launcherIniFile;
	}

	/**
	 * Den Inhalt des ini-Files zurueckgeben.
	 * 
	 * @param systemCommandLine
	 * @return
	 * @throws Exception
	 */
	public String getLauncherIniFileContent() throws Exception
	{
		String commandLine = null;
		iniFile = getLauncherIniFile();		
		if ((iniFile != null) && (iniFile.exists()))
			commandLine = FileUtils.readFileToString(iniFile);
		return commandLine;
	}

	/*
	 * entfernt CMD_DATA mit Pfadangabe in einer Zeile
	 */
	private Map<String, String> removeInlineDataKey(Map<String, String>parseMap)
	{
		List<String> removeKeys = new ArrayList<String>();
		for (String key : parseMap.keySet())
		{
			if (StringUtils.startsWith(key, CMD_DATA)
					&& (StringUtils.length(key) > StringUtils.length(CMD_DATA)))
				removeKeys.add(key);
		}
		for(String key : removeKeys)
			parseMap.remove(key);
			
		return parseMap;
	}
	
	
	/*
	 * 
	 */
	private String createIniContent(Map<String, String>parseMap)
	{
		StringBuilder content = new StringBuilder();		
		for(String key : parseMap.keySet())
		{
			String value = parseMap.get(key);
			content.append(key);
			content.append(NEW_LINE);
			if(StringUtils.isNotEmpty(value))
			{
				content.append(value);
				content.append(NEW_LINE);				
			}
		}
		
		return content.toString();
	}

	/**
	 * Ein INI_CMD.DATA Eintrag in die Launcher-Ini Datei eintragen.
	 * Ein bestehender Eintrag wird aktualisiert.
	 * Es ist sichergestellt, dass der Entrag vor einem INI_CMD.VMARGS eingetragen wird.
	 * 
	 * @param workspaces
	 * @throws Exception
	 */
	public void setDATA_CMDWorkspaceIniOLD(String workspacePath) throws Exception
	{		
		int startPos, endPos;
		String check;
		File iniFile = getLauncherIniFile();
		String iniFileContent = getLauncherIniFileContent();
		
		if (StringUtils.isNotEmpty(iniFileContent))
		{
			StringBuilder builder = new StringBuilder(iniFileContent);
			
			// der neue Eintrag bekommt einen Zeilenvorschub
			workspacePath = workspacePath + NEW_LINE;
			
			// existiert ein INI_CMD.DATA - Command, dann bestehenden '-data' Eintrag aktualisieren
			startPos = getIniCommand(iniFileContent, INI_CMD.DATA);
			if(startPos > 0)
			{
				// Start des Eintrags beginnt hinter dem INI_CMD.DATA 				
				startPos = startPos + StringUtils.length(getINICommand(INI_CMD.DATA));
				// ein evtl. vorhandener Zeilenvorschub beim Start des Eintrags gehoert mit zum INI_CMD				
				check = StringUtils.substring(iniFileContent, startPos, startPos+1);
				if(StringUtils.equals(check, NEW_LINE))
					startPos++;
								
				// der Eintrag endet am Beginn des naechsten INI_CMD 
				endPos = isIniCommand(StringUtils.substring(iniFileContent, startPos));
				if(endPos > 0)
				{
					// absolute Endposition des Eintrags
					endPos = startPos + endPos;
				}
				else
				{				
					// sollte kein weiterer INI_CMD nachfolgen, endet den Eintrag am Dateiende
					endPos = StringUtils.length(iniFileContent);
				}
				
				// alten Eintrag loeschen
				builder.delete(startPos, endPos);
				
				// neuen Eintrag einfuegen				
				builder.insert(startPos, workspacePath);				
				
				FileUtils.writeStringToFile(iniFile, builder.toString());
				return;
			}
			
			// existiert ein INI_CMD.VMARGS - Command, dann muss der neue '-data' Eintrag vor diesem eingefuegt werden
			startPos = getIniCommand(iniFileContent, INI_CMD.VMARGS);
			if(startPos > 0)
			{				
				builder.insert(startPos, getINICommand(INI_CMD.DATA));	
				startPos = startPos + StringUtils.length(getINICommand(INI_CMD.DATA));
				builder.insert(startPos, NEW_LINE);
				startPos = startPos + StringUtils.length(NEW_LINE);
				builder.insert(startPos, workspacePath);
				
				FileUtils.writeStringToFile(iniFile, builder.toString());				
				return;
			}
			
			// INI_CMD.DATA Eintrag hinzufuegen
			builder.append(CMD_DATA);
			builder.append(NEW_LINE);
			builder.append(workspacePath);
			FileUtils.writeStringToFile(iniFile, builder.toString());
		}
	}
	
	/*
	 * Gibt es in dem String 'iniFileContent' ein CMD_INI Command, wenn ja, wird die Startposition zureckgegeben.
	 * Kein Command resultiert in der Rueckgabe von (-1)
	 */
	private int isIniCommand(String iniFileContent)
	{
		int pos;
		for(INI_CMD cmd : INI_CMD.values())
		{
			pos = StringUtils.indexOf(iniFileContent, getINICommand(cmd));
			if(pos > 0)
				return pos;
		}
		
		return (-1);
	}
	
	/*
	 * Startposition eines INI_CMD im 'iniFileContent' zuruckgeben.
	 * Rueckgabe (-1) wenn kein Command vorhanden
	 */
	private int getIniCommand(String iniFileContent, INI_CMD iniCmd)
	{
		return StringUtils.indexOf(iniFileContent, getINICommand(iniCmd));
	}


	public void setWorkspaceLocations(String [] workspaces) throws Exception
	{
		String systemCommandLine = System.getProperty(PROP_COMMANDS);
		String initCommandLine = getLauncherIniFileContent(systemCommandLine);

		if (StringUtils.isNotEmpty(initCommandLine))
		{
			// die Properties aller '-data' Commands akkumulieren
			int cmd_data_pos = StringUtils.indexOf(initCommandLine, CMD_DATA);
			if (cmd_data_pos != -1)
			{
				String prop = initCommandLine.substring(cmd_data_pos);
				initCommandLine = StringUtils.remove(initCommandLine, prop);
			}

			StringBuilder result = new StringBuilder(initCommandLine);
			for (String data : workspaces)
			{
				result.append(CMD_DATA);
				result.append(NEW_LINE);
				result.append(data);
				result.append(NEW_LINE);
			}

			File iniFile = getLauncherIniFile(systemCommandLine);
			FileUtils.writeStringToFile(iniFile, result.toString());
		}
	}
	
	public void setConfigContent(String content)
	{		
		try
		{
			File iniFile = getLauncherIniFile();
			FileUtils.writeStringToFile(iniFile, content);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Alle im ini-File gespeicherten '-data' Properties auslesen. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String [] getWorkspaceLocations() throws Exception
	{
		String systemCommandLine = System.getProperty(PROP_COMMANDS);
		String initCommandLine = getLauncherIniFileContent(systemCommandLine);
		
		if (StringUtils.isNotEmpty(initCommandLine))
		{
			// die Properties aller '-data' Commands akkumulieren
			int pos = StringUtils.indexOf(initCommandLine, CMD_DATA);
			if (pos != (-1))
			{
				String dataCommandLine = StringUtils.substring(initCommandLine,pos);
				String[] dataProps = StringUtils.split(dataCommandLine,COMMAND_PREFIX);
							
				for(int i = 0;i < dataProps.length;i++)
				{
					dataProps[i] = StringUtils.remove(dataProps[i], StringUtils.substring(CMD_DATA,1));
					dataProps[i] = StringUtils.replace(dataProps[i], NEW_LINE, "");						
				}

				return dataProps;
			}
		}
		
		return null;
	}
	
	/**
	 * Den Inhalt des ini-Files zurueckgeben.
	 * 
	 * @param systemCommandLine
	 * @return
	 * @throws Exception
	 */
	public String getLauncherIniFileContent(String systemCommandLine) throws Exception
	{
		String commandLine = null;
		File file = getLauncherIniFile(systemCommandLine);
		if ((file != null) && (file.exists()))
			commandLine = FileUtils.readFileToString(file);
		return commandLine;
	}


	/**
	 * Rueckgabe des Inhalts eines ini-Konfigurationsfile in einem String
	 * 
	 * @return
	 */
	public String getConfigFileContentAsString()
	{
		try
		{
			File iniFile = getLauncherIniFile();
			if ((iniFile != null) && (iniFile.exists()))
				return FileUtils.readFileToString(iniFile);
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void setJPIPEConfigurationEntryOLD(String oldJPIPEPath, String newJPIPEPath)
	{
		String stgConfigContent = getConfigFileContentAsString();
		if(StringUtils.isEmpty(stgConfigContent))
		{
			MessageDialog.openError(Display.getDefault().getActiveShell(), "LibreOffice", "in der IDE nicht möglich"); //$NON-NLS-N$
			return;
		}
		
		StringBuilder configContent = new StringBuilder(getConfigFileContentAsString());
		
		if(oldJPIPEPath == null)
		{
			// Eintrag existiert noch nicht, hinzufuegen
			configContent.append(CMD_VMARGS);
			configContent.append(NEW_LINE);
			configContent.append("-");
			configContent.append(DJAVA_LIBRARY_PATH);
			configContent.append("=");
			configContent.append(newJPIPEPath);
		}
		else
		{
			// vorhandenen Eintrag aktualisieren
			int start = configContent.indexOf(oldJPIPEPath);
			configContent.insert(start, newJPIPEPath);
			
			start = start + newJPIPEPath.length();
			configContent.delete(start, start+oldJPIPEPath.length());			
		}
		
		// geanderten Inhalt zureuckschreiben
		setConfigContent(configContent.toString());
	}
	
	/**
	 * Rueckgabe aller im Konfigurationsfile unter '-Djava.library.path' definierten Pfade.
	 * 
	 * @return
	 */
	public String [] getConfigLibraryPaths()
	{
		String [] libraryPaths = null;

		try
		{
			File iniFile = getLauncherIniFile();
			if ((iniFile != null) && (iniFile.exists()))
			{
				String content = FileUtils.readFileToString(iniFile);
				String [] commands = StringUtils.split(content, "-"); 
				
				for(String cmd : commands)
				{
					if (StringUtils.startsWith(cmd,DJAVA_LIBRARY_PATH))
					{
						String libraryPath = StringUtils.substringAfter(cmd,"=");
						libraryPath = StringUtils.strip(libraryPath, "\n");
						libraryPaths = (String[]) ArrayUtils.add(libraryPaths,libraryPath);
						
						//libraryPaths = (String[]) ArrayUtils.add(libraryPaths, StringUtils.substringAfter(cmd,"="));
					}
				}
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return libraryPaths;
	}

	/**
	 * Die Location des momentan genutzten Workspace zurueckgeben. Die Information wird
	 * aus dem System - Property 'eclipse.commands' ausgelesen.
	 * 
	 * @return
	 */
	public String getCurrentWorkspaceLocation()
	{	
		String location = getProperty(System.getProperty(PROP_COMMANDS), CMD_DATA);
		if(StringUtils.isEmpty(location))
		{
			File file = getLauncherIniFile(System.getProperty(PROP_COMMANDS));
			file = new File(file.getParentFile(),"workspace");
			location = file.getPath();
		}
		
		return location;
	}
	
	public String getProperty(String commandLine, String command)
	{
		String prop = null;

		int pos = StringUtils.indexOf(commandLine, command);
		if (pos != (-1))
		{
			prop = StringUtils.substring(commandLine, pos + command.length());
			if (prop.startsWith(NEW_LINE))
				prop = StringUtils.substring(prop, 1);
			pos = StringUtils.indexOf(prop, NEW_LINE);
			if (pos != (-1))
				prop = StringUtils.substring(prop, 0, pos);	
		}
		return prop;
	}	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	
	
	
	public String getCommandLine()
	{
		String [] commandLineArray = getCommandLineArray();
		return StringUtils.join(commandLineArray);
	}	

	public String getLauncher()
	{
		String [] commandLineArray = getCommandLineArray();
		return findProperty(commandLineArray, CMD_LAUNCHER);
	}	

	

	/**
	 * Rueckgabe des Inifiles.
	 * 
	 * @return
	 */
	public File getLauncherIniFileOLDOLD()
	{
		String baseName;
		File launcherIniFile = null;
		String [] commandLineArray = getCommandLineArray();		
		String launcherPath = findProperty(commandLineArray, CMD_LAUNCHER);
		if(StringUtils.isNotEmpty(launcherPath))
		{
			baseName = FilenameUtils.getBaseName(launcherPath)+".ini";			
			File launcherFile = new File(launcherPath);			
			if(launcherFile.isDirectory())
				launcherIniFile = new File(launcherFile,baseName);
			else
			{				
				String path = launcherFile.getParent();				
				launcherIniFile = new File(path,baseName);
			}
						
			if(!launcherIniFile.exists())
				return null;
		}
		return launcherIniFile;
	}
	
	/**
	 * Die aktuelle Workspacelocation aus dem Systemcommand auslesen
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCommandlineWorkspaceLocation()
	{
		String [] commandLineArray = getCommandLineArray();		
		return findProperty(commandLineArray, CMD_DATA);
	}
	
	
	public String getApplicationNameProperty()
	{
		String [] commandLineArray = getCommandLineArray();		
		return findProperty(commandLineArray, CMD_NAME);
	}
	

	
	/*
	 * Properties des Systemcommands
	 */
	private String [] getCommandLineArray()
	{
		String property = System.getProperty(PROP_COMMANDS);
		if (StringUtils.isNotEmpty(property))
		{
			property = property.replace(NEW_LINE, "");
			return StringUtils.split(property, COMMAND_PREFIX);
		}
		return null;
	}
	
	



	private String findProperty(String [] commands,String command)
	{
		if(!ArrayUtils.isEmpty(commands))
		{
			for(String cmd : commands)
			{
				if(StringUtils.startsWith(cmd, command))				
					return(StringUtils.substring(cmd, command.length()));				
			}
		}
				
		return null;
	}

	private String [] findAllProperties(String [] commands,String command)
	{
		String [] properties = null;
		
		if(!ArrayUtils.isEmpty(commands))
		{
			for(String cmd : commands)
			{
				if(StringUtils.startsWith(cmd, command))
					properties = (String[]) ArrayUtils.add(properties, StringUtils.substring(cmd, command.length()));				
			}
		}
				
		return properties;
	}


	
	
	
	
	public File getLauncherIniFileOLD()
	{
		File launcherIniFile = null;
		String property = System.getProperty(PROP_COMMANDS);
		if (StringUtils.isNotEmpty(property))
		{
			int cmd_launcher_pos = property.lastIndexOf(CMD_LAUNCHER);
			if (cmd_launcher_pos != -1)
			{
				String prop = getLauncher(property);
				launcherIniFile = new File(prop+".ini"); //$NON-NLS-N$
				if(!launcherIniFile.exists())
					return null;
			}
		}

		return launcherIniFile;
	}	

	/**
	 * token '-launcher' kann mehrmals auftauchen, deshalb diese Funktion zur sicheren Selektion 
	 */
	private String getLauncher(String property)
	{
		String retval = null;
		String [] propArray = StringUtils.split(property, NEW_LINE);
		int idx = ArrayUtils.indexOf(propArray, CMD_LAUNCHER);
		if(idx >= 0)
			retval = propArray[idx+1];		
		return retval;
	}

	/*
	public String getWorkspaceLocationProperty()
	{
		String property = System.getProperty(PROP_COMMANDS);
		if (property != null)
		{
			int cmd_pos = property.lastIndexOf(CMD_DATA);
			if (cmd_pos != -1)
			{
				cmd_pos += CMD_DATA.length() + 1;
				String prop = property.substring(cmd_pos);
				return prop.substring(0, prop.indexOf(NEW_LINE));				
			}
		}		
		return null;
	}
	*/
	
	/*
	public String getApplicationNameProperty()
	{
		String property = System.getProperty(PROP_COMMANDS);
		if (property != null)
		{
			int cmd_pos = property.lastIndexOf(CMD_NAME);
			if (cmd_pos != -1)
			{
				cmd_pos += CMD_NAME.length() + 1;
				String prop = property.substring(cmd_pos);
				return prop.substring(0, prop.indexOf(NEW_LINE));				
			}
		}		
		return null;
	}
	*/
	
	/*
	public void setWorkspaceLocations(String [] workspaces) throws Exception
	{
		File fileIni = null;
		if (fileIni.exists())
		{			
			String property = FileUtils.readFileToString(fileIni);
			int cmd_data_pos = property.indexOf(CMD_DATA);
			if (cmd_data_pos != -1)
			{
				String prop = property.substring(cmd_data_pos);
				property = StringUtils.remove(property, prop);
			}
			
			StringBuilder result = new StringBuilder(property);
			for(String data : workspaces)
			{
				result.append(CMD_DATA);
				result.append(NEW_LINE);
				result.append(data);
				result.append(NEW_LINE);
			}
			
			FileUtils.writeStringToFile(fileIni, result.toString());
		}

	}
	*/
	
	/*
	public String[] getWorkspaceLocations()
	{				
		String [] propArray = StringUtils.split(System.getProperty(PROP_COMMANDS), NEW_LINE);
		String [] workspaces = null;
		
		for(int i = 0;i < propArray.length;i++)
		{
			if(StringUtils.equals(propArray[i], CMD_DATA))
				workspaces = (String[]) ArrayUtils.add(workspaces, propArray[++i]);
		}
		
		return workspaces;
	}
	*/

	
}
